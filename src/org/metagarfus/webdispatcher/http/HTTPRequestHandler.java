package org.metagarfus.webdispatcher.http;

import org.metagarfus.webdispatcher.Utils;
import org.metagarfus.webdispatcher.WebServerApplication;
import org.metagarfus.webdispatcher.html.HTMLQuery;
import org.metagarfus.webdispatcher.html.Mime;
import org.metagarfus.webdispatcher.log.LogType;
import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.metagarfus.webdispatcher.log.WebDispatcherLog;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

public class HTTPRequestHandler implements HttpRequestHandler {
    private final WebServerApplication application;
    private final WebDispatcherLog logger;
    private final String COOKIE_SET_HEADER = "Set-Cookie";
    private final String COOKIE_GET_HEADER = "Cookie";
    private final static ContentType CONTENT_TYPE_TEXT_XML_UTF8 = ContentType.create("text/html", "UTF-8");

    public HTTPRequestHandler(WebServerApplication application) {
        super();
        this.application = application;
        this.logger = this.application.logger;
    }

    private void openFile(final HttpResponse response, String target) {
        FileEntity fileContent;
        File file;
        file = new File(target.substring(1));
        if (!validatePath(response, file, application.config.locations.resources))
            return;
        if (file.exists() && file.canRead()) {
            fileContent = new FileEntity(file);
            fileContent.setContentType(Mime.getType(target));
            response.setEntity(fileContent);
        } else {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
        }
    }

    private boolean validatePath(HttpResponse response, File file, String allowedPath) {
        if (Utils.isNullOrEmpty(allowedPath))
            return false;
        try {
            allowedPath = new File(allowedPath).getAbsolutePath();
            logger.log("Allowed path: " + allowedPath);
            logger.log("Requested path: " + file.getAbsolutePath());
            if (!file.getAbsolutePath().startsWith(allowedPath)) {
                response.setStatusCode(HttpStatus.SC_FORBIDDEN);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {
        final String target;
        final String method;
        final HTMLQuery query;
        method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if (!method.equals("POST") && !method.equals("GET")) {
            throw new MethodNotSupportedException(method + " method not supported");
        }
        query = new HTMLQuery();
        query.fromString(request.getRequestLine().getUri());
        target = query.path;
        logger.log(LogType.HTTP, "target = " + target);
        final StringBuilder resourcesPrefix = new StringBuilder(Utils.emptyIfNull(application.config.locations.resources));
        if (resourcesPrefix.length() > 0) {
            if (resourcesPrefix.charAt(0) != '/')
                resourcesPrefix.insert(0, '/');
            if (resourcesPrefix.charAt(resourcesPrefix.length() - 1) != '/')
                resourcesPrefix.append("/");
            if (target.startsWith(resourcesPrefix.toString())) {
                openFile(response, target);
                return;
            }
        }
        if (!getPage(request, response, target, query))
            return;
        logger.log(LogType.HTTP, "Responding ...");
    }

    private boolean getPage(HttpRequest request, HttpResponse response, String target, HTMLQuery query) throws IOException {
        try {
            HttpEntity entity;
            String html;
            StringEntity body;
            logger.log(LogType.HTTP, request.toString());
            parseFormData(request, query);
            String sessionID = getSessionID(request);
            if (sessionID == null || sessionID.isEmpty())
                sessionID = UUID.randomUUID().toString();
            response.setStatusCode(HttpStatus.SC_OK);
            html = application.dispatcher.getResponseContent(target, sessionID, query.values);
            logger.log(LogType.HTTP, "Response constructed");
            if (html == null) {
                response.setStatusCode(HttpStatus.SC_NOT_FOUND);
                return false;
            }
            body = new StringEntity(html, CONTENT_TYPE_TEXT_XML_UTF8);
            body.setContentType("text/html");
            response.setHeader(COOKIE_SET_HEADER, String.format("sessionId=%s", sessionID));
            response.setEntity(body);
            return true;
        } catch (Throwable e) {
            response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            return false;
        }
    }

    private byte[] parseFormData(HttpRequest request, HTMLQuery query) throws IOException {
        HttpEntity entity;
        byte[] entityContent =  new byte[0];
        if (request instanceof HttpEntityEnclosingRequest) {
            entity = ((HttpEntityEnclosingRequest) request).getEntity();
            entityContent = EntityUtils.toByteArray(entity);
            query.addFormData(new String(entityContent));
            logger.log(LogType.HTTP, "Incoming entity content (bytes): " + entityContent.length);
        }
        logger.log(LogType.HTTP, "Entity: " + new String(entityContent));
        return entityContent;
    }

    private String getSessionID(HttpRequest request) {
        final Header[] cookies = request.getHeaders(COOKIE_GET_HEADER);
        String sessionId = null;
        for (Header cookie : cookies) {
            final String[] cookiesValues = cookie.getValue().split(";", -1);
            for (String cookiesValue : cookiesValues) {
                final String[] parts = cookiesValue.split("=", -1);
                if (parts.length < 2)
                    continue;
                if (parts[0].trim().equals("sessionId") && !parts[1].trim().isEmpty()) {
                    sessionId = parts[1].trim();
                    break;
                }
            }
        }
        return sessionId;
    }
}

package org.metagarfus.webdispatcher.http;

import org.metagarfus.webdispatcher.WebServerApplication;
import org.metagarfus.webdispatcher.content.WebContent;
import org.metagarfus.webdispatcher.log.LogType;
import org.metagarfus.webdispatcher.log.WebDispatcherLog;

import java.util.HashMap;
import java.util.Set;

public class RequestDispatcher {
    public static final String DESPERATE_MESSAGE = "<html><head>\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
            "    <title>Mistakes were made</title>\n" +
            "    <link href=\"resources/css/main.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
            "    <body><center><table class='full_page'><tr><td class='fatal_error' valign='middle'>\n" +
            "     Ups! You were not supposed to see this! Anyway the page crashed... \n" +
            "   </td></tr></table></center></body><html>";
    private final HashMap<String, ResponseFactory> factories;
    private final WebServerApplication application;
    private final WebDispatcherLog logger;

    public RequestDispatcher(WebServerApplication application) {
        this.application = application;
        this.logger = this.application.logger;
        this.factories = new HashMap<>();
    }

    public void registerPath(String path, ResponseFactory factory) {
        if (!path.startsWith("/"))
            path = "/" + path;
        factories.put(path, factory);
        logger.log(LogType.INIT, String.format("Registered %s on path '%s'", factory, path));
    }

    public String getResponseContent(String path, String sessionID, HashMap<String, String> values) {
        ResponseFactory factory;
        factory = factories.get(path);
        if (factory == null)
            return null;
        final String content;
        try {
            content = factory.generateContent(sessionID, values);
        } catch (Throwable e) {
            e.printStackTrace();
            application.logger.errorLog(LogType.HTTP, e.getMessage());
            return DESPERATE_MESSAGE;
        }
        return content;
    }

    public void registerWebContent() {
        if (application.reflections == null)
            return;
        Set<Class<? extends ResponseFactory>> responseFactories = application.reflections.getSubTypesOf(ResponseFactory.class);
        for (Class<? extends ResponseFactory> responseFactory : responseFactories) {
            final WebContent annotation = responseFactory.getAnnotation(WebContent.class);
            if (annotation == null)
                continue;
            try {
                final ResponseFactory factory = responseFactory.newInstance();
                factory.setWebServerApplication(application);
                registerPath(annotation.path(), factory);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}

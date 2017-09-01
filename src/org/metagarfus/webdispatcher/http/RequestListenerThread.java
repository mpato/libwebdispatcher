package org.metagarfus.webdispatcher.http;

import org.metagarfus.webdispatcher.WebServerApplication;
import org.metagarfus.webdispatcher.log.LogType;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.*;
import org.metagarfus.webdispatcher.log.WebDispatcherLog;
import org.metagarfus.webdispatcher.Utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RequestListenerThread extends Thread {
    private final ServerSocket serversocket;
    private final HttpParams params;
    private final HttpService httpService;
    private final WebServerApplication application;
    private final WebDispatcherLog logger;

    public RequestListenerThread(WebServerApplication application) throws IOException {
        this.application = application;
        logger = this.application.logger;
        final int port = this.application.config.server.port;
        logger.log(LogType.INIT, String.format("Using port %d", port));
        this.serversocket = new ServerSocket(port);
        this.params = new SyncBasicHttpParams();
        this.params/*.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)*/
                .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
                .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                .setParameter(CoreProtocolPNames.ORIGIN_SERVER, Utils.emptyIfNull(application.config.server.name));

        // Set up the HTTP protocol processor
        HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpResponseInterceptor[]{
                new ResponseDate(),
                new ResponseServer(),
                new ResponseContent(),
                new ResponseConnControl()
        });

        // Set up request handlers
        HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
        reqistry.register("*", new HTTPRequestHandler(application));

        // Set up the HTTP service
        this.httpService = new HttpService(
                httpproc,
                new DefaultConnectionReuseStrategy(),
                new DefaultHttpResponseFactory(),
                reqistry,
                this.params);
    }

    @Override
    public void run() {
        logger.log(LogType.HTTP, "Listening on port " + this.serversocket.getLocalPort());
        while (!Thread.interrupted()) {
            try {
                // Set up HTTP connection
                Socket socket = this.serversocket.accept();
                DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                System.out.println("Incoming connection from " + socket.getInetAddress());
                conn.bind(socket, this.params);

                // Start worker thread
                Thread t = new WorkerThread(this.httpService, conn);
                t.setDaemon(true);
                t.start();
            } catch (InterruptedIOException ex) {
                break;
            } catch (IOException e) {
                logger.errorLog(LogType.HTTP, "I/O error initialising connection thread: " + e.getMessage());
                break;
            }
        }
    }

    public int getServerPort() {
        if (this.serversocket == null)
            return -1;
        return this.serversocket.getLocalPort();
    }

    public void close() throws IOException {
        interrupt();
        if (this.serversocket == null)
            return;
        this.serversocket.close();
    }
}

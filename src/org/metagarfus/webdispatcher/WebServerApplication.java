package org.metagarfus.webdispatcher;

import org.metagarfus.webdispatcher.http.RequestListenerThread;
import org.metagarfus.webdispatcher.http.RequestDispatcher;
import org.metagarfus.webdispatcher.log.WebDispatcherLog;
import org.metagarfus.webdispatcher.log.LogType;
import org.metagarfus.webdispatcher.task.ResultStatus;
import org.metagarfus.webdispatcher.task.ServerSideTask;
import org.metagarfus.webdispatcher.task.ServerSideTaskManager;
import org.reflections.Reflections;

import java.io.IOException;

public class WebServerApplication {
    private RequestListenerThread requestsThread;
    public final ServerSideTaskManager<ServerSideTask> buildManager = new ServerSideTaskManager<>(1);
    public final WebDispatcherConfig config;
    public final RequestDispatcher dispatcher;
    public final Reflections reflections;
    public final WebDispatcherLog logger;

    public WebServerApplication(WebDispatcherConfig config) {
        if (config == null)
            throw new IllegalArgumentException("WebServerApplication: Config cannot be null");
        this.config = config;
        this.logger = new WebDispatcherLog(this, config.files.log);
        if (config.application.webContentPackage != null)
            this.reflections = new Reflections(config.application.webContentPackage);
        else
            this.reflections = null;
        this.dispatcher = new RequestDispatcher(this);
    }

    public boolean start() {
        try {
            this.dispatcher.registerWebContent();
            if (requestsThread != null) {
                logger.log(LogType.HTTP, "HTTP Server already running", ResultStatus.FAILED);
                return false;
            }
            try {
                requestsThread = new RequestListenerThread(this);
            } catch (IOException e) {
                e.printStackTrace();
                logger.log(LogType.HTTP, "Failed to init HTTP Server", ResultStatus.FAILED);
                return false;
            }
            requestsThread.setDaemon(config.server.isDaemon);
            requestsThread.start();
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getServerPort() {
        if (this.requestsThread == null)
            return -1;
        return this.requestsThread.getServerPort();
    }

    public void stop() {
        try {
            if (requestsThread != null)
                requestsThread.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}

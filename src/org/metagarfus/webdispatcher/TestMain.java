package org.metagarfus.webdispatcher;

import javafx.application.Application;
import javafx.geometry.Point2D;
import org.metagarfus.webdispatcher.browser.WebViewApplication;
import org.metagarfus.webdispatcher.http.ResponseFactory;

import java.util.HashMap;

public class TestMain extends WebViewApplication {
    private WebServerApplication serverApplication;

    @Override
    protected void onBeforeStart() {
        final WebDispatcherConfig config = new WebDispatcherConfig();
        config.server.port = 0;
        config.server.name = "TEST Server/1.0";
        config.server.isDaemon = true;
        config.locations.resources = "resources";
        serverApplication = new WebServerApplication(config);
        serverApplication.dispatcher.registerPath("/hello", new ResponseFactory() {
            @Override
            public void setWebServerApplication(WebServerApplication application) {

            }

            @Override
            public String generateContent(String sessionID, HashMap<String, String> values) {
                return "<html><body><h1>HELLO WORLD</h1></body></html>";
            }
        });
        serverApplication.start();
        setTitle("Test Client ");
        setURL("http://localhost:" + serverApplication.getServerPort() + "/hello");
        setWindowDimensions(new Point2D(700, 700));
    }

    @Override
    protected void onStop() {
        // you can stop the server here, if you like, but it is better to set isDaemon to true
        //  serverApplication.stop();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}

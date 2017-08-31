package org.metagarfus.webdispatcher;

import org.metagarfus.webdispatcher.http.ResponseFactory;

import java.util.HashMap;

public class TestMain {

    public static void main(String[] args) {
        final WebDispatcherConfig config = new WebDispatcherConfig();
        config.server.port = 8090;
        config.server.name = "TEST Server/1.0";
        config.locations.resources = "resources";
        final WebServerApplication application = new WebServerApplication(config);
        application.dispatcher.registerPath("/hello", new ResponseFactory() {
            @Override
            public void setWebServerApplication(WebServerApplication application) {

            }

            @Override
            public String generateContent(String sessionID, HashMap<String, String> values) {
                return "<html><body><h1>HELLO WORLD</h1></body></html>";
            }
        });
        application.start();

    }

}

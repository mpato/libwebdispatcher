package org.metagarfus.webdispatcher.http;

import org.metagarfus.webdispatcher.WebServerApplication;

import java.util.HashMap;

public interface ResponseFactory {
    void setWebServerApplication(WebServerApplication application);
    String generateContent(String sessionID, HashMap<String, String> values);
}

package org.metagarfus.webdispatcher.browser;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class WebViewApplication extends Application {

    private String title = "";
    private String URL = "http://localhost:8085";
    private Point2D windowDimensions;

    protected abstract void onBeforeStart();
    protected abstract void onStop();

    @Override
    public void start(Stage primaryStage) throws Exception {
        onBeforeStart();
        primaryStage.setTitle(title);
        final Browser browser = new Browser(URL);
        if (windowDimensions == null)
            windowDimensions = new Point2D(300, 300);
        final Scene scene = new Scene(browser, windowDimensions.getX(), windowDimensions.getY());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setWindowDimensions(Point2D windowDimensions) {
        this.windowDimensions = windowDimensions;
    }

    @Override
    public void stop(){
        onStop();
    }
}

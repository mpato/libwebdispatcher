package org.metagarfus.webdispatcher.log;

import org.metagarfus.webdispatcher.WebServerApplication;
import org.metagarfus.webdispatcher.task.ResultStatus;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

public class WebDispatcherLog {
    private final WebServerApplication application;
    public FileWriter writer;

    public WebDispatcherLog(WebServerApplication application){
       this(application, "log.txt");
    }

    public WebDispatcherLog(WebServerApplication application, String location) {
        this.application = application;
        try {
            writer = new FileWriter(location);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void log(String message) {
        log(LogType.DEBUG, message, ResultStatus.UNKNOWN);
    }

    public synchronized void log(LogType type, String message) {
        log(type, message, ResultStatus.UNKNOWN);
    }

    public synchronized void errorLog(LogType type, String message) {
        log(type, message, ResultStatus.FAILED);
    }

    public synchronized void log(LogType type, String message, ResultStatus status) {
        final PrintStream out;
        if (application.config.logs.ignore != null && application.config.logs.ignore.contains(type.name()))
            return;
        if (status == ResultStatus.FAILED)
            out = System.err;
        else
            out = System.out;
        final String error;
        switch (status) {
            case FAILED:
                error = "[E]";
                break;
            case UNKNOWN:
                error = "[N]";
                break;
            default:
                error = "[S]";
        }
        String output = String.format("[%s]%s %s: %s", new Date(), error, type, message);
        out.println(output);
        writeToFile(output, status);
    }

    private void writeToFile(String output, ResultStatus status) {
        if (writer == null)
            return;
        try {
            writer.write(String.format("%s\r\n",output));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package org.metagarfus.webdispatcher.task;

import org.metagarfus.webdispatcher.WebServerApplication;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class ServerSideTask implements Callable<Result> {
    protected Result result;
    protected Future future;
    protected ServerSideTask parentTask;
    protected final WebServerApplication application;

    protected ServerSideTask(WebServerApplication application) {
        this.application = application;
    }

    protected abstract Result executeTask();

    public Result getResult() {
        return result;
    }

    public final Result execute() {
        result = executeTask();
        if (result == null)
            result = Result.EMPTY;
        return result;
    }

    @Override
    public Result call() throws Exception {
        return execute();
    }

    public void executeInExecutor(ExecutorService executor) {
        future = executor.submit(this);
    }

    public boolean isDone() {
        return future == null || future.isDone();
    }

    public boolean isCancelled() {
        return future != null && future.isCancelled();
    }

    public void cancel() {
        if (future == null)
            return;
        future.cancel(true);
        close();
    }


    public ServerSideTask getParentTask() {
        return parentTask;
    }

    public void setParentTask(ServerSideTask parentTask) {
        this.parentTask = parentTask;
    }

    public void close() {
    }
}

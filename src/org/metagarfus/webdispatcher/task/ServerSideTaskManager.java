package org.metagarfus.webdispatcher.task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSideTaskManager<T extends ServerSideTask> {
    protected Map<String, T> tasks = new HashMap<>();
    protected ExecutorService executor;

    public ServerSideTaskManager(int nThreads) {
        executor = Executors.newFixedThreadPool(nThreads);
    }

    public synchronized void addTask(String key, T task) {
        cancel(key);
        task.executeInExecutor(executor);
        tasks.put(key, task);
    }

    private synchronized void cancel(String key) {
        final ServerSideTask currentTask = tasks.get(key);
        if (currentTask !=  null)
            currentTask.cancel();
    }

    public synchronized T getTask(String key) {
        return tasks.get(key);
    }

    public synchronized void clearTask(String key) {
        cancel(key);
        tasks.remove(key);
    }

    public synchronized boolean isDone(String key) {
        final ServerSideTask currentTask = tasks.get(key);
        if (currentTask ==  null)
            return true;
        return currentTask.isDone();
    }
}

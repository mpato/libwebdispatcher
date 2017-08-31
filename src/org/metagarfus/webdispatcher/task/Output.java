package org.metagarfus.webdispatcher.task;

public class Output <T> {
    public Result result;
    public T data;

    public Output(Result result, T data) {
        this.result = result;
        this.data = data;
    }
}

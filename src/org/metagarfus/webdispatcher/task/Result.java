package org.metagarfus.webdispatcher.task;

public class Result {
    public static final Result EMPTY = new Result(ResultStatus.UNKNOWN, "");
    public static final Result SUCCESS = new Result(ResultStatus.SUCCESS, "");
    public static final Result INTERRUPTED = new Result(ResultStatus.FAILED, "INTERRUPTED");
    public static final Result EXCEPTION = new Result(ResultStatus.FAILED, "EXCEPTION");

    public ResultStatus status;
    public String message;

    public Result(ResultStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String toString() {
        switch(status) {
            case SUCCESS:
                return "SUCCESS";
            case UNKNOWN:
                return "Unknown error";
            default:
                return message;
        }
    }
}

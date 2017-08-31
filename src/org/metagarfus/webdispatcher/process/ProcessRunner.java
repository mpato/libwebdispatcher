package org.metagarfus.webdispatcher.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;

public class ProcessRunner<T> {

    public interface ProcessListener<T> {
        T onRun(Process p, BufferedReader reader) throws Exception;
    }

    private ProcessListener<T> listener;
    private File workingDir;
    private T defaultResult;

    public ProcessRunner(ProcessListener<T> listener) {
        this.listener = listener;
    }
    public void setWorkingDir(File workingDir) {
        this.workingDir = workingDir;
    }

    public void setDefaultResult(T defaultResult) {
        this.defaultResult = defaultResult;
    }

    public final T run(String... args) {
        return run(workingDir, defaultResult, args);
    }

    public final T run(File dir, T defaultResult, String... args) {
        ProcessBuilder pb = new ProcessBuilder(args);
        Map<String, String> env = pb.environment();
        pb.directory(dir);
        pb.redirectErrorStream(true);
        T result = defaultResult;
        try {
            Process p = pb.start();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                if (listener != null)
                    result = listener.onRun(p, reader);
            } finally {
                if (reader != null)
                    reader.close();
                p.destroyForcibly();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

package org.metagarfus.webdispatcher;

import java.io.*;

public class Utils {

    public static String emptyIfNull(String value) {
        return value != null ? value : "";
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static void closeObject(Closeable object) {
        try {
            if (object != null)
                object.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                if (!deleteRecursive(child))
                    return false;
            }
        }
        return fileOrDirectory.delete();
    }
}

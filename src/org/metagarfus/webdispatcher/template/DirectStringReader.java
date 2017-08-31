package org.metagarfus.webdispatcher.template;

import java.io.IOException;
import java.io.Reader;

public class DirectStringReader extends Reader {
    private String value;
    private int position;

    public DirectStringReader(String value) {
        this.value = value;
    }

    public int read() throws IOException {
        try {
            return value.charAt(position++);
        } catch (Throwable e) {
            return -1;
        }
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
      reset();
    }

    public void reset() throws IOException {
        position = 0;
    }
}

package org.metagarfus.webdispatcher.template;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TemplateBuilder implements Evaluable{

    public static final int STATE_NONE = 0;
    protected static final int STATE_MAY_START = 1;
    protected static final int STATE_KEY = 2;
    protected static final int STATE_MAY_END = 3;
    protected final Map<String, Object> mapping = new HashMap<>();
    protected File template;
    protected final TemplateCache templateCache;
    protected Reader cachedReader;
    private int bufferCapacity = 1024;

    public TemplateBuilder(File template) {
        this(template, null);
    }

    public TemplateBuilder(File template, TemplateCache templateCache) {
        this.template = template;
        this.templateCache = templateCache;
    }

    public void add(String key, String value) {
        mapping.put(key, value);
    }

    public void add(String key, Evaluable value) {
        mapping.put(key, value);
    }

    public void add(String key, Iterable value) {
        mapping.put(key, value);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(bufferCapacity);
        evaluate(builder);
        final String result = builder.toString();
        builder.setLength(0);
        builder.trimToSize();
        return result;
    }

    public void evaluate(StringBuilder builder) {
        Reader reader = null;
        try {
            if (templateCache != null && cachedReader == null)
                cachedReader = templateCache.getReader(template);
            if (cachedReader != null) {
                cachedReader.reset();
                reader = cachedReader;
            } else
                reader = new BufferedReader(new FileReader(template));
            replaceMappings(reader, builder);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeReader(reader);
        }
    }

    protected void replaceMappings(Reader reader, StringBuilder builder) {
        final StringBuilder key = new StringBuilder();;
        int character, state = STATE_NONE;
        try {
            while((character = reader.read()) >= 0) {
                switch (state) {
                    case STATE_NONE:
                        if (character == '#')
                            state++;
                        else {
                            builder.append((char) character);
                        }
                        break;
                    case STATE_MAY_START:
                        if (character != '#') {
                            builder.append('#');
                            builder.append((char) character);
                            state--;
                        } else {
                            key.setLength(0);
                            state++;
                        }
                        break;
                    case STATE_KEY:
                        if (character == '#')
                            state++;
                        else {
                            key.append((char) character);
                        }
                        break;
                    case STATE_MAY_END:
                        if (character != '#') {
                            key.append('#');
                            key.append((char) character);
                            state--;
                        } else {
                            evaluateMapping(builder,  mapping.get(key.toString()));
                            state = STATE_NONE;
                        }
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void evaluateMapping(StringBuilder builder, Object mapping) {
        if (mapping == null)
            mapping = "";
        if (mapping instanceof String)
            builder.append(mapping);
        else if (mapping instanceof Iterable)
            iterateMapping((Iterable) mapping, builder);
        else if (mapping instanceof Evaluable)
            ((Evaluable) mapping).evaluate(builder);
        else
            builder.append(mapping.toString());
    }

    private void iterateMapping(Iterable mapping, StringBuilder builder) {
        for (Object item : mapping)
            evaluateMapping(builder, item);
    }

    protected void closeReader(Reader reader) {
        if (reader != null)
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void setBufferCapacity(int bufferCapacity) {
        this.bufferCapacity = bufferCapacity;
    }
}

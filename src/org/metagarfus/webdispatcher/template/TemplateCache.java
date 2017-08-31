package org.metagarfus.webdispatcher.template;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class TemplateCache {
    private Map<String, String> cache = new HashMap<>();;

    protected Reader getReader(File template) throws IOException {
        final String absolutePath = template.getAbsolutePath();
        String value = cache.get(absolutePath);
        if (value == null) {
            value = FileUtils.readFileToString(template);
            cache.put(absolutePath, value);
        }
        return new DirectStringReader(value);
    }

    public void clear() {
        cache.clear();
    }
}

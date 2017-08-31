package org.metagarfus.webdispatcher.html;

import org.metagarfus.webdispatcher.template.TemplateBuilder;
import org.metagarfus.webdispatcher.template.TemplateCache;

import java.io.File;

public class HTMLTemplateBuilder extends TemplateBuilder{
    public HTMLTemplateBuilder(File template) {
        super(template);
    }

    public HTMLTemplateBuilder(File template, TemplateCache templateCache) {
        super(template, templateCache);
    }
}

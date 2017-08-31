package org.metagarfus.webdispatcher.template;

public interface Evaluable {
    Evaluable EMPTY = builder -> {};
    void evaluate(StringBuilder builder);
}

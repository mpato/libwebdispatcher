package org.metagarfus.webdispatcher.template;

public class StringEvaluable implements Evaluable {
    private String value;

    public StringEvaluable(String value) {
        this.value = value;
    }

    @Override
    public void evaluate(StringBuilder builder) {
        builder.append(value);
    }
}

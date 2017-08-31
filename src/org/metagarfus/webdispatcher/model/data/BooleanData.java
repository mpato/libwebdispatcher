package org.metagarfus.webdispatcher.model.data;

public class BooleanData {
    protected String name;
    protected String value;
    protected boolean checked;

    public BooleanData(String name, String value, boolean checked) {
        this.name = name;
        this.value = value;
        this.checked = checked;
    }

    @Override
    public String toString() {
        return toString(name, value, checked);
    }

    public static String toString(String name, String value, boolean checked) {
        return String.format("<input type='checkbox' name='%s' value='%s' %s>", name, value, checked ? "checked" : "");
    }
}

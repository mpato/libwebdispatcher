package org.metagarfus.webdispatcher.model.data;

public class EditableText {
    protected final String name;
    protected final String value;
    protected final boolean isMultiLine;

    public EditableText(String name, String value, boolean isMultiLine) {
        this.name = name;
        this.value = value;
        this.isMultiLine = isMultiLine;
    }

    @Override
    public String toString() {
        return toString(name, value, isMultiLine);
    }

    public static String toString(String name, String value, boolean isMultiLine) {
        final String input;
        if (isMultiLine)
            input = "<textarea name='%s' id='%s' style='display:table-cell; width:100%%; height:100%%'>%s</textarea>";
        else
            input = "<input type='text' name='%s' id='%s' value='%s' style='display:table-cell; width:100%%'>";
        return String.format(input, name, "edittext-" + name, value);
    }
}

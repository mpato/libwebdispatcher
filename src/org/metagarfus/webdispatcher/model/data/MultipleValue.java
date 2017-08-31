package org.metagarfus.webdispatcher.model.data;

import java.util.ArrayList;
import java.util.List;

public class MultipleValue {
    private class Option {
        public String value;
        public String description;

        public Option(String value, String description) {
            this.value = value;
            this.description = description;
        }
    }
    protected List<Option> options = new ArrayList<>();
    protected String selectedValue;
    protected String name;

    public MultipleValue(String name) {
        this.name = name;
    }

    public void add(String value, String description) {
        options.add(new Option(value, description));
    }

    public void setSelectedValue(String value) {
        selectedValue = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("<select name='%s' style=\"min-width:90%%;\">", name));
        for (Option option : options) {
            final String selected = selectedValue != null && selectedValue.equals(option.value) ? "selected" : "";
            builder.append(String.format("<option value='%s' %s>%s</option>", option.value, selected, option.description));
        }
        builder.append("</select>");
        return builder.toString();
    }
}

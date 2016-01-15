package org.wwarn.jira.report.domain;

import java.util.List;

/**
 * Created by suay on 1/15/16.
 */
public class CustomField {
    String name;

    List<String> values;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "CustomField{" +
                "name='" + name + '\'' +
                ", values=" + values +
                '}';
    }
}

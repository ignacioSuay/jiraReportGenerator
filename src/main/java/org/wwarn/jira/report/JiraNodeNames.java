package org.wwarn.jira.report;

/**
 * Created by suay on 1/13/16.
 */
public enum JiraNodeNames {
    ITEM("item"),
    SUMMARY("summary"),
    PROJECT("project"),
    LINK("link"),
    TYPE("type"),
    PRIORITY("priority"),
    STATUS("status"),
    RESOLUTION("resolution"),
    ASSIGNEE("assignee")
    ;


    String name;

    JiraNodeNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
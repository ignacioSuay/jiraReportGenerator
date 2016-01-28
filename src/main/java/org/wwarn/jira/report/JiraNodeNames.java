package org.wwarn.jira.report;

/**
 * Created by suay on 1/13/16.
 */
public enum JiraNodeNames {
    TITLE("title"),
    ITEM("item"),
    SUMMARY("summary"),
    PROJECT("project"),
    LINK("link"),
    KEY("key"),
    TYPE("type"),
    PRIORITY("priority"),
    STATUS("status"),
    RESOLUTION("resolution"),
    ASSIGNEE("assignee"),
    REPORTER("reporter"),
    CREATED("created"),
    UPDATED("updated"),
    TIME_ESTIMATE("timeestimate"),
    CUSTOM_FIELDS("customfields"),
    CUSTOM_FIELD("customfield"),
    CUSTOM_FIELD_NAME("customfieldname"),
    CUSTOM_FIELD_VALUE("customfieldvalue"),
    SPRINT("Sprint"),
    EPIC_LINK("Epic Link");


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

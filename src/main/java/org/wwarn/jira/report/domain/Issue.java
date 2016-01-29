package org.wwarn.jira.report.domain;

import org.wwarn.jira.report.JiraNode;

import java.util.Date;
import java.util.List;


/**
 * Created by suay on 1/13/16.
 */
public class Issue {

    String title;

    String summary;

    String project;

    String link;

    String key;

    String type;

    String priority;

    String status;

    String resolution;

    String assignee;

    String reporter;

    Date created;

    Date updated;

    String timeEstimate;

    List<CustomField> customFields;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getTimeEstimate() {
        return timeEstimate;
    }

    public void setTimeEstimate(String timeEstimate) {
        this.timeEstimate = timeEstimate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<CustomField> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<CustomField> customFields) {
        this.customFields = customFields;
    }

    public String getValueByNode(JiraNode node){
        String res = null;

        switch (node){
            case TITLE:
                res = getTitle();break;
            case SUMMARY:
                res = getSummary();break;
            case PROJECT:
                res = getProject();break;
            case LINK:
                res = getLink();break;
            case TYPE:
                res = getType();break;
            case PRIORITY:
                res = getPriority();break;
            case STATUS:
                res = getStatus();break;
            case RESOLUTION:
                res = getResolution();break;
            case ASSIGNEE:
                res = getAssignee();break;
            case REPORTER:
                res = getReporter();break;
            case CREATED:
                res = getCreated().toString();break;
            case UPDATED:
                res = getUpdated().toString();break;
            case TIME_ESTIMATE:
                res = getSummary();break;
            case SPRINT:
                res = getCustomFieldValue(JiraNode.SPRINT.getName());break;
            case EPIC_LINK:
                res = getCustomFieldValue(JiraNode.EPIC_LINK.getName());break;

        }

        return res;
    }

    public String getCustomFieldValue(String field){
        return customFields.stream()
                .filter(cf -> field.equals(cf.getName()))
                .map(CustomField::getValue)
                .findFirst().orElse("not found");

    }

    @Override
    public String toString() {
        return "IssueDTO{" +
                "title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", project='" + project + '\'' +
                ", link='" + link + '\'' +
                ", type='" + type + '\'' +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                ", resolution='" + resolution + '\'' +
                ", assignee='" + assignee + '\'' +
                ", reporter='" + reporter + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", timeEstimate='" + timeEstimate + '\'' +
                '}';
    }
}

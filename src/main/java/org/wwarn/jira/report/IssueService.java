package org.wwarn.jira.report;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wwarn.jira.report.domain.CustomField;
import org.wwarn.jira.report.domain.Issue;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by suay on 2/2/16.
 */
@Component
public class IssueService {

    public List<Issue> jiraToIssueDTO(FileInputStream inputStream) throws IOException, SAXException {
        List<Issue> issues = new ArrayList<Issue>();
        InputSource inputSource = new InputSource(inputStream);
        DOMParser parser = new DOMParser();
        parser.parse(inputSource);
        Document xmlDoc = parser.getDocument();

        final Element root= xmlDoc.getDocumentElement();
        NodeList items = root.getElementsByTagName(JiraNode.ITEM.getName());

        for (int i = 0; i < items.getLength(); i++){
            Element item = (Element)items.item(i);
            issues.add(XmlElementToIssue(item));
        }
        return issues;
    }

    private Issue XmlElementToIssue(Element item){
        Issue issue = new Issue();
        issue.setTitle(getNodeValue(item, JiraNode.TITLE.name).trim());
        issue.setLink(getNodeValue(item, JiraNode.LINK.name).trim());
        issue.setProject(getNodeValue(item, JiraNode.PROJECT.name).trim());
        issue.setSummary(getNodeValue(item, JiraNode.SUMMARY.name).trim());
        issue.setKey(getNodeValue(item, JiraNode.KEY.name).trim());
        issue.setType(getNodeValue(item, JiraNode.TYPE.name).trim());
        issue.setPriority(getNodeValue(item, JiraNode.PRIORITY.name).trim());
        issue.setStatus(getNodeValue(item, JiraNode.STATUS.name).trim());
        issue.setResolution(getNodeValue(item, JiraNode.RESOLUTION.name).trim());
        issue.setAssignee(getNodeValue(item, JiraNode.ASSIGNEE.name).trim());
        issue.setReporter(getNodeValue(item, JiraNode.REPORTER.name).trim());
        issue.setTimeEstimate(getNodeValue(item, JiraNode.TIME_ESTIMATE.name).trim());
        String estimateInSec = getAttributeValue(item, JiraNode.TIME_ESTIMATE.name, JiraNode.SECONDS.name);
        if(!estimateInSec.isEmpty()) issue.setTimeEstimateInSeconds(Integer.parseInt(estimateInSec));

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ");
            issue.setCreated(formatter.parse(getNodeValue(item, JiraNode.CREATED.name)));
            issue.setUpdated(formatter.parse(getNodeValue(item, JiraNode.UPDATED.name)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        issue.setCustomFields(getCustomFields(item));
        return issue;
    }

    private List<CustomField> getCustomFields(Element item){
        NodeList customFieldsList = item.getElementsByTagName(JiraNode.CUSTOM_FIELD.name);
        List<CustomField> customFields = new ArrayList<>();
        for (int i = 0; i < customFieldsList.getLength(); i++){
            Element cf = (Element)customFieldsList.item(i);
            CustomField customField = new CustomField();
            customField.setName(getNodeValue(cf, JiraNode.CUSTOM_FIELD_NAME.name));
            customField.setValue(getNodeValue(cf, JiraNode.CUSTOM_FIELD_VALUE.name));
            customFields.add(customField);
        }
        return customFields;
    }

    private String getNodeValue(Element record, String tagName){
        if(record.getElementsByTagName(tagName).getLength() >0)
            return record.getElementsByTagName(tagName).item(0).getFirstChild().getNodeValue();

        return "";
    }

    private String getAttributeValue(Element record, String tagName, String attribute){
        if(record.getElementsByTagName(tagName).getLength() >0)
            return record.getElementsByTagName(tagName).item(0).getAttributes().getNamedItem(attribute).getNodeValue();

        return "";
    }

    public Map<String, List<Issue>> groupIssuesBy(List<Issue>issues, JiraNode jiraNodeName){
        return issues.stream().collect(Collectors.groupingBy(i -> i.getValueByNode(jiraNodeName)));
    }
}

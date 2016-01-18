package org.wwarn.jira.report;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
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

/**
 * Created by suay on 1/13/16.
 */
public class ReadJiraXmlService {


    public static List<Issue> jiraToIssueDTO(FileInputStream inputStream) throws IOException, SAXException {
        List<Issue> issues = new ArrayList<Issue>();
        InputSource inputSource = new InputSource(inputStream);
        DOMParser parser = new DOMParser();
        parser.parse(inputSource);
        Document xmlDoc = parser.getDocument();

        final Element root= xmlDoc.getDocumentElement();
        NodeList items = root.getElementsByTagName(JiraNodeNames.ITEM.getName());

        for (int i = 0; i < items.getLength(); i++){
            Element item = (Element)items.item(i);
            issues.add(XmlElementToIssue(item));
        }
        return issues;
    }


    private static Issue XmlElementToIssue(Element item){
        Issue issue = new Issue();
        issue.setTitle(getNodeValue(item, JiraNodeNames.TITLE.name).trim());
        issue.setLink(getNodeValue(item, JiraNodeNames.LINK.name).trim());
        issue.setProject(getNodeValue(item, JiraNodeNames.PROJECT.name).trim());
        issue.setSummary(getNodeValue(item, JiraNodeNames.SUMMARY.name).trim());
        issue.setType(getNodeValue(item, JiraNodeNames.TYPE.name).trim());
        issue.setPriority(getNodeValue(item, JiraNodeNames.PRIORITY.name).trim());
        issue.setStatus(getNodeValue(item, JiraNodeNames.STATUS.name).trim());
        issue.setResolution(getNodeValue(item, JiraNodeNames.RESOLUTION.name).trim());
        issue.setAssignee(getNodeValue(item, JiraNodeNames.ASSIGNEE.name).trim());
        issue.setReporter(getNodeValue(item, JiraNodeNames.REPORTER.name).trim());
        issue.setTimeEstimate(getNodeValue(item, JiraNodeNames.TIME_ESTIMATE.name).trim());

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ");
            issue.setCreated(formatter.parse(getNodeValue(item, JiraNodeNames.CREATED.name)));
            issue.setUpdated(formatter.parse(getNodeValue(item, JiraNodeNames.UPDATED.name)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        issue.setCustomFields(getCustomFields(item));
        String sprint = issue.getCustomFieldValue("sprint");
        return issue;
    }

    private static List<CustomField> getCustomFields(Element item){
        NodeList customFieldsList = item.getElementsByTagName(JiraNodeNames.CUSTOM_FIELD.name);
        List<CustomField> customFields = new ArrayList<>();
        for (int i = 0; i < customFieldsList.getLength(); i++){
            Element cf = (Element)customFieldsList.item(i);
            CustomField customField = new CustomField();
            customField.setName(getNodeValue(cf, JiraNodeNames.CUSTOM_FIELD_NAME.name));
            customField.setValue(getNodeValue(cf, JiraNodeNames.CUSTOM_FIELD_VALUE.name));
            customFields.add(customField);
        }
        return customFields;
    }

    private static String getNodeValue(Element record, String tagName){
        if(record.getElementsByTagName(tagName).getLength() >0)
            return record.getElementsByTagName(tagName).item(0).getFirstChild().getNodeValue();

        return "";
    }
}

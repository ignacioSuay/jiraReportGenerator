package org.wwarn.jira.report;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wwarn.jira.report.domain.CustomField;
import org.wwarn.jira.report.domain.Issue;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by suay on 1/13/16.
 */
@Component
public class ReportService {


    public void createWordDocument(List<Issue> issues) throws IOException {
        XWPFDocument doc = new XWPFDocument();

        XWPFTable table = doc.createTable(issues.size()+1, 3);

        table.getRow(0).getCell(0).setText("Issue Title");
        table.getRow(0).getCell(1).setText("Assignee");
        table.getRow(0).getCell(2).setText("Estimate");

        for (int i = 0; i < issues.size(); i++){
            Issue issue = issues.get(i);
            int row = i + 1;
            XWPFParagraph p1 = table.getRow(row).getCell(0).getParagraphs().get(0);
            XWPFRun r1 = p1.createRun();
            r1.setBold(true);
            r1.setText(issue.getTitle());
//            table.getRow(row).getCell(0).setText(issue.getTitle());
            table.getRow(row).getCell(1).setText(issue.getAssignee());
            table.getRow(row).getCell(2).setText(issue.getTimeEstimate());

        }

        FileOutputStream out = new FileOutputStream("simple.docx");
        doc.write(out);
        out.close();

    }

    public void createTableByFields(List<Issue> issues, List<JiraNode> fields) throws IOException {
        Resource resource = new ClassPathResource("template.docx");
        XWPFDocument doc = new XWPFDocument(resource.getInputStream());

        XWPFTable table = doc.createTable(issues.size()+1, fields.size());
        table.setStyleID("LightShading-Accent1");

        for(int cols = 0; cols < fields.size(); cols++){
            XWPFParagraph p1 = table.getRow(0).getCell(cols).getParagraphs().get(0);
            XWPFRun r1 = p1.createRun();
            r1.setBold(true);
            r1.setText(fields.get(cols).getName().toUpperCase());
            r1.setItalic(true);
//            r1.setColor("90C3D4");

        }


        for (int i = 0; i < issues.size(); i++){
            Issue issue = issues.get(i);
            int row = i + 1;
            for(int cols = 0; cols < fields.size(); cols++){
                table.getRow(row).getCell(cols).setText(issue.getValueByNode(fields.get(cols)));
            }
        }

        FileOutputStream out = new FileOutputStream("simple.docx");
        doc.write(out);
        out.close();

    }


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

    public Map<String, List<Issue>> groupIssuesBy(List<Issue>issues, JiraNode jiraNodeName){
        return issues.stream().collect(Collectors.groupingBy(i-> i.getValueByNode(jiraNodeName)));
    }
}

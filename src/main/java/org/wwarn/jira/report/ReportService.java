package org.wwarn.jira.report;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by suay on 1/13/16.
 */
@Component
public class ReportService {

    @Autowired
    IssueService issueService;

    public void createWordDocument(List<Issue> issues) throws IOException {
        Resource resource = new ClassPathResource("template.docx");
        XWPFDocument doc = new XWPFDocument(resource.getInputStream());

        addSection(doc, "Summary");
        createSummaryTable(issues,doc);

        List<JiraNode> fields = Arrays.asList(JiraNode.TITLE, JiraNode.ASSIGNEE, JiraNode.CREATED, JiraNode.SPRINT, JiraNode.EPIC_LINK);
        createTableByFields(issues, fields, doc);

        FileOutputStream out = new FileOutputStream("simple.docx");
        doc.write(out);
        out.close();

    }

    public void createTableByFields(List<Issue> issues, List<JiraNode> fields, XWPFDocument doc) throws IOException {

        XWPFTable table = doc.createTable(issues.size()+1, fields.size());
        table.setStyleID("LightShading-Accent1");

        for(int cols = 0; cols < fields.size(); cols++){
            XWPFParagraph p1 = table.getRow(0).getCell(cols).getParagraphs().get(0);
            XWPFRun r1 = p1.createRun();
            r1.setBold(true);
            r1.setText(fields.get(cols).getName().toUpperCase());
            r1.setItalic(true);
        }

        for (int i = 0; i < issues.size(); i++){
            Issue issue = issues.get(i);
            int row = i + 1;
            for(int cols = 0; cols < fields.size(); cols++){
                table.getRow(row).getCell(cols).setText(issue.getValueByNode(fields.get(cols)));
            }
        }
    }

    public void createSummaryTable(List<Issue> issues, XWPFDocument doc){

        Map<String, Integer> collect = issues.stream().collect(Collectors.groupingBy(i -> i.getValueByNode(JiraNode.EPIC_LINK),
                Collectors.summingInt(Issue::getTimeEstimateInSeconds)));

        XWPFTable table = doc.createTable(collect.keySet().size()+1, 2);
        table.setStyleID("LightShading-Accent1");

        table.getRow(0).getCell(0).setText("Epic");
        table.getRow(0).getCell(0).setText("Time");

        int row = 1;
        for(String key: collect.keySet()){
            table.getRow(row).getCell(0).setText(key);
            table.getRow(row).getCell(1).setText(collect.get(key).toString());
            row++;
        }


    }

    public void addSection(XWPFDocument doc, String title){
        XWPFParagraph p = doc.createParagraph();
        p.setStyle("Heading1");
        XWPFRun r1 = p.createRun();
        r1.setText(title);
    }





}

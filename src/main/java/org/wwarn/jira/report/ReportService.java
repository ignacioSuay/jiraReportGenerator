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
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
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

        addSection(doc, "Epic Summary");
        createSummaryTable(issues,doc);

        addSection(doc, "Tasks completed by Assignee");
        createAssigneeTable(issues, doc);

        addSection(doc, "List of all issues");
        List<JiraNode> fields = Arrays.asList(JiraNode.TITLE, JiraNode.ASSIGNEE, JiraNode.CREATED, JiraNode.SPRINT, JiraNode.EPIC_LINK);
        createTableByFields(issues, fields, doc);

        FileOutputStream out = new FileOutputStream("simple.docx");
        doc.write(out);
        out.close();

    }


    public void createTableByFields(List<Issue> issues, List<JiraNode> fields, XWPFDocument doc) throws IOException {

        XWPFTable table = doc.createTable(issues.size()+1, fields.size());
        table.setStyleID("LightShading-Accent1");
        table.getCTTbl().getTblPr().unsetTblBorders();

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

        Map<String, Integer> collect = issues.stream().filter(i-> !i.isEpic()).collect(Collectors.groupingBy(i -> i.getValueByNode(JiraNode.EPIC_LINK),
                Collectors.summingInt(Issue::getTimeEstimateInSeconds)));

        XWPFTable table = doc.createTable(collect.keySet().size()+1, 2);
        table.setStyleID("LightShading-Accent1");
        table.getCTTbl().getTblPr().unsetTblBorders();

        table.getRow(0).getCell(0).setText("Epic title");
        table.getRow(0).getCell(1).setText("Time");

        int row = 1;
        for(String key: collect.keySet()){
            String epicTitle = getEpicTitle(issues, key);
            table.getRow(row).getCell(0).setText(epicTitle);
            int timeInSeconds = collect.get(key);
            table.getRow(row).getCell(1).setText(secondsToDDHH(timeInSeconds));
            row++;
        }
    }

    public void createAssigneeTable(List<Issue> issues, XWPFDocument doc) {
        Map<String, List<Issue>> collect = issues.stream()
                .filter(i -> !i.isEpic())
                .collect(Collectors.groupingBy(i -> i.getValueByNode(JiraNode.ASSIGNEE)));


        for(String assignee: collect.keySet()){
            addSubSection(doc, assignee + " tasks");
            XWPFTable table = doc.createTable(collect.get(assignee).size()+2, 3);
            table.getCTTbl().getTblPr().unsetTblBorders();
            table.setStyleID("LightShading-Accent1");

            table.getRow(0).getCell(0).setText("Epic");
            table.getRow(0).getCell(1).setText("Task");
            table.getRow(0).getCell(2).setText("Estimated Time");
            int row = 1;
            List<Issue> issuesPerAssignee = collect.get(assignee);
            for(Issue issue: issuesPerAssignee){
                String epicTitle = getEpicTitle(issues, issue.getValueByNode(JiraNode.EPIC_LINK));
                table.getRow(row).getCell(0).setText(epicTitle);
                table.getRow(row).getCell(1).setText(issue.getTitleName());
                table.getRow(row).getCell(2).setText(issue.getTimeOriginalEstimate());
                row++;
            }
            table.getRow(row).getCell(0).setText("Total");
            table.getRow(row).getCell(1).setText("");
            Integer totalTime = issuesPerAssignee.stream().collect(Collectors.summingInt(Issue::getTimeEstimateInSeconds));
            table.getRow(row).getCell(2).setText(secondsToDDHH(totalTime));

        }
    }

    private String getEpicTitle(List<Issue> issues, String key) {
        if("not found".equals(key))
            return "unassigned";

        return issues.stream().filter(i -> "Epic".equals(i.getType()))
                .filter(i -> i.getKey().equals(key))
                .map(Issue::getTitleName)
                .findFirst()
                .orElse("unassigned");
    }

    private String secondsToDDHH(int seconds){
        long totalHours = TimeUnit.SECONDS.toHours(seconds);
        int day = (int) (totalHours / 8);
        long hours = totalHours % 8;

        StringBuilder stringBuilder = new StringBuilder();
        if(day>0) stringBuilder.append(day + " days ");
        if(hours>0) stringBuilder.append(hours + " hours ");
        return stringBuilder.toString();

    }

    public void addSection(XWPFDocument doc, String title){
        XWPFParagraph p = doc.createParagraph();
        p.setStyle("Heading1");
        XWPFRun r1 = p.createRun();
        r1.setText(title);
        r1.addBreak();
    }

    public void addSubSection(XWPFDocument doc, String title){
        XWPFParagraph p = doc.createParagraph();
        p.setStyle("Heading2");
        XWPFRun r1 = p.createRun();
        r1.setText(title);
    }





}

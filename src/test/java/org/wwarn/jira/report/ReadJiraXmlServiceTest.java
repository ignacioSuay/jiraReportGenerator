package org.wwarn.jira.report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wwarn.jira.report.domain.Issue;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;


/**
 * Created by suay on 1/15/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class ReadJiraXmlServiceTest {


    @Autowired
    ReadJiraXmlService readJiraXmlService;

    @Test
    public void testJiraToIssueDTO() throws Exception{
        File file = new File("/home/suay/dev/jiraReportGenerator/src/test/resources/sprint6.xml");
        FileInputStream f = new FileInputStream(file);
        List<Issue> issueList = readJiraXmlService.jiraToIssueDTO(f);
        assertNotNull(issueList);

    }

    @Test
    public void testCreateWordDocument() throws Exception {
        File file = new File("/home/suay/dev/jiraReportGenerator/src/test/resources/sprint6.xml");
        FileInputStream f = new FileInputStream(file);
        List<Issue> issueList = readJiraXmlService.jiraToIssueDTO(f);
        readJiraXmlService.createWordDocument(issueList);
    }

    @Test
    public void testCreateTableByFields() throws Exception {
        File file = new File("/home/suay/dev/jiraReportGenerator/src/test/resources/sprint6.xml");
        FileInputStream f = new FileInputStream(file);
        List<JiraNode> fields = Arrays.asList(JiraNode.TITLE, JiraNode.ASSIGNEE, JiraNode.CREATED, JiraNode.SPRINT, JiraNode.EPIC_LINK);
        List<Issue> issueList = readJiraXmlService.jiraToIssueDTO(f);
        readJiraXmlService.createTableByFields(issueList, fields);
    }


    @Test
    public void testGroupIssuesBy() throws Exception {
        File file = new File("/home/suay/dev/jiraReportGenerator/src/test/resources/sprint7.xml");
        FileInputStream f = new FileInputStream(file);
        List<JiraNode> fields = Arrays.asList(JiraNode.TITLE, JiraNode.ASSIGNEE, JiraNode.CREATED, JiraNode.SPRINT, JiraNode.EPIC_LINK);
        List<Issue> issueList = readJiraXmlService.jiraToIssueDTO(f);
        Map<String, List<Issue>> stringListMap = readJiraXmlService.groupIssuesBy(issueList, JiraNode.ASSIGNEE);
        assert stringListMap != null;
    }
}

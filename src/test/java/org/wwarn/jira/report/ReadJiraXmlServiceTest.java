package org.wwarn.jira.report;

import org.junit.Test;
import org.wwarn.jira.report.domain.Issue;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;


/**
 * Created by suay on 1/15/16.
 */
public class ReadJiraXmlServiceTest {


    @Test
    public void testJiraToIssueDTO() throws Exception{
        File file = new File("/home/suay/dev/jiraReportGenerator/src/test/resources/sprint6.xml");
        FileInputStream f = new FileInputStream(file);
        List<Issue> issueList = ReadJiraXmlService.jiraToIssueDTO(f);
        assertNotNull(issueList);

    }

    @Test
    public void testCreateWordDocument() throws Exception {
        File file = new File("/home/suay/dev/jiraReportGenerator/src/test/resources/sprint6.xml");
        FileInputStream f = new FileInputStream(file);
        List<Issue> issueList = ReadJiraXmlService.jiraToIssueDTO(f);
        ReadJiraXmlService.createWordDocument(issueList);
    }
}

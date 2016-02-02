package org.wwarn.jira.report;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wwarn.jira.report.domain.Issue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;


/**
 * Created by suay on 1/15/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class ReportServiceTest {

    @Autowired
    IssueService issueService;

    @Autowired
    ReportService reportService;


    @Test
    public void testCreateWordDocument() throws Exception {
        List<Issue> issueList = getListIssues();
        reportService.createWordDocument(issueList);
    }


    private List<Issue> getListIssues() throws Exception {
        File file = new File("/home/suay/dev/jiraReportGenerator/src/test/resources/sprint7.xml");
        FileInputStream f = new FileInputStream(file);
        List<Issue> issueList = issueService.jiraToIssueDTO(f);
        return issueList;
    }

}

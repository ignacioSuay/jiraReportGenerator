package org.wwarn.jira.report;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;


/**
 * Created by suay on 1/15/16.
 */
public class ReadJiraXmlServiceTest {


    @Test
    public void testJiraToIssueDTO() throws Exception{
        File file = new File("/home/suay/dev/jiraReportGenerator/src/test/resources/sprint6.xml");
        FileInputStream f = new FileInputStream(file);
        ReadJiraXmlService.jiraToIssueDTO(f);
    }
}

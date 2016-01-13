package org.wwarn.jira.report;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suay on 1/13/16.
 */
public class ReadJiraXmlService {

    public static final String ITEM = "item";

    public static List<IssueDTO> jiraToIssueDTO(FileInputStream inputStream) throws IOException, SAXException {
        List<IssueDTO> issues = new ArrayList<IssueDTO>();
        InputSource inputSource = new InputSource(inputStream);
        DOMParser parser = new DOMParser();
        parser.parse(inputSource);
        Document xmlDoc = parser.getDocument();

        final Element root= xmlDoc.getDocumentElement();
        NodeList records = root.getElementsByTagName(ITEM);

        for (int i = 0; i < records.getLength(); i++){
            IssueDTO issueDTO = new IssueDTO();
            issues.add(issueDTO);
        }
        return issues;
    }
}

import core.postprocessor.ReportProcessor;
import core.postprocessor.SlackProcessor;
import model.Result;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class MainTest {

    @Test
    public void mainTest() throws IOException {
        Result result = new Result();

        TestrailMain initTestrail = new TestrailMain();
        result.testrailResult = initTestrail.mainMethod();

        GsheetMain initGsheet = new GsheetMain();
        result.sheetResult = initGsheet.mainMethod();

        JiraMain initJira = new JiraMain();
        result.jiraResult = initJira.mainMethod();

        ReportProcessor reportProcessor = new ReportProcessor(result);
        reportProcessor.displayData();
        boolean checkResult = reportProcessor.verifyData();

        try {
            Assert.assertTrue(checkResult);
        } catch (AssertionError e) {
            SlackProcessor.sendSlackNotification("{\"text\":\"Testing Akbar\"}");
            Assert.fail("The count did not match");
        }
    }
}

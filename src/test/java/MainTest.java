import core.Config;
import core.postprocessor.ReportProcessor;
import model.Result;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class MainTest {

    @Test
    public void mainTest() throws IOException {
        Result result = new Result();

        init_testrail initTestrail = new init_testrail();
        result.testrailResult = initTestrail.mainMethod();

        init_gsheet initGsheet = new init_gsheet();
        result.sheetResult = initGsheet.mainMethod();

        init_jira initJira = new init_jira();
        result.jiraResult = initJira.mainMethod();

        ReportProcessor reportProcessor = new ReportProcessor(result);
        reportProcessor.displayData();
        boolean checkResult = reportProcessor.verifyData();

        Assert.assertTrue(checkResult);
    }
}

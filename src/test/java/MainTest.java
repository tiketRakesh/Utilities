import core.Config;
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
            String messagePayload =
                    "{" +
                            "\"blocks\":[{" +
                                "\"type\":\"section\"," +
                                "\"text\":{" +
                                    "\"type\":\"mrkdwn\"," +
                                    "\"text\":\"" + Config.PLATFORM + "-" + Config.VERTICAL + "-" + Config.ENVIRONMENT + " is having difference in count @here\"" +
                                "}," +
                                "\"accessory\":{" +
                                    "\"type\":\"button\"," +
                                    "\"text\":{" +
                                        "\"type\":\"plain_text\"," +
                                        "\"text\":\"CheckCount\"," +
                                        "\"emoji\":true" +
                                    "}," +
                                    "\"value\":\"JenkinsJob\"," +
                                    "\"url\":\"https://selena.ggwp.red/job/Automation%20Count%20Checker/" + Config.JENKINS_BUILD_NUMBER + "/console\"," +
                                    "\"action_id\":\"button-action\"" +
                                "}" +
                            "}]" +
                    "}";

            SlackProcessor.sendSlackNotification(messagePayload);
            Assert.fail("The count did not match");
        }
    }
}

import core.Config;
import core.testRail.TestRailAPI;
import model.Result;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestrailMain {
    public Result.Testrail mainMethod() {
        String baseUrl = "https://tiket.testrail.com";
        String username = Config.TESTRAIL_USERNAME;
        String apiKey = Config.TESTRAIL_PASSWORD;
        Result.Testrail testrailResult = new Result.Testrail();

        TestRailAPI testRailAPI = new TestRailAPI(baseUrl, username, apiKey);

        int projectId = Config.TESTRAIL_PROJECT_ID;
        int suiteId = Config.TESTRAIL_SUITE_ID;
        List<Integer> sectionIds = Config.TESTRAIL_SECTION_IDs;

        int customAutomationType = model.Testrail.AutomationType.YES.getCode();
        List<Integer> customAutomationStatus =  Arrays.asList(model.Testrail.AutomationStatus.DONE.getCode(), model.Testrail.AutomationStatus.DO_AGAIN.getCode());
        int customTcStatus = model.Testrail.TestCaseStatus.APPROVED.getCode();

        Map<String, List<Map<String, Object>>> groupedCases = testRailAPI.fetchAndGroupTestCases(
                projectId, suiteId, sectionIds, customAutomationType, customAutomationStatus,customTcStatus);

        testrailResult.automationStatusDoneDoAgainCount = groupedCases.get("all").size();
        testrailResult.automationStatusDoAgainCount = groupedCases.get(String.valueOf(model.Testrail.AutomationStatus.DO_AGAIN.getCode())).size();

        return testrailResult;
    }
}

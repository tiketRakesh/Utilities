package core.testRail;

import core.Config;
import core.testRail.TestRailAPI;
import model.testMappingValidator.Result;
import model.testMappingValidator.Testrail;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestRailMain {
    public Result.Testrail mainMethod() {
        String baseUrl = "https://tiket.testrail.com";
        String username = Config.TESTRAIL_USERNAME;
        String apiKey = Config.TESTRAIL_PASSWORD;
        Result.Testrail testrailResult = new Result.Testrail();

        TestRailAPI testRailAPI = new TestRailAPI(baseUrl, username, apiKey);

        int projectId = Config.TESTRAIL_PROJECT_ID;
        int suiteId = Config.TESTRAIL_SUITE_ID;
        List<Integer> sectionIds = Config.TESTRAIL_SECTION_IDs;

        int customAutomationType = Testrail.AutomationType.YES.getCode();
        List<Integer> customAutomationStatus =  Arrays.asList(Testrail.AutomationStatus.DONE.getCode(), Testrail.AutomationStatus.DO_AGAIN.getCode());
        int customTcStatus = Testrail.TestCaseStatus.APPROVED.getCode();

        Map<String, List<Map<String, Object>>> groupedCases = testRailAPI.fetchAndGroupTestCases(
                projectId, suiteId, sectionIds, customAutomationType, customAutomationStatus,customTcStatus);

        testrailResult.automationStatusDoneDoAgainCount = groupedCases.get("all").size();
        testrailResult.automationStatusDoAgainCount = groupedCases.get(String.valueOf(Testrail.AutomationStatus.DO_AGAIN.getCode())).size();
        testrailResult.groupedCasesData = groupedCases;

        return testrailResult;
    }
}

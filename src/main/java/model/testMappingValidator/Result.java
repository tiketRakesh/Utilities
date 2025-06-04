package model.testMappingValidator;

import java.util.List;
import java.util.Map;

public class Result {
    public Sheet sheetResult;
    public Testrail testrailResult;
    public Jira jiraResult;

    public static class Sheet {
        public int totalRow;
        public int sheetShouldRunYCount;
        public int validTestIdsExtracted;
        public int sheetEnvironmentYCount;
        public Map<String, List<Object>> sheetData;
        public List<String> sheetLeftover;
        public List<String> missingTestData;
        public List<String> wrongTestData;
    }

    public static class Testrail {
        public int automationStatusDoAgainCount;
        public int automationStatusDoneDoAgainCount;
        public Map<String, List<Map<String, Object>>> groupedCasesData;
        public List<String> testrailLeftover;
    }

    public static class Jira {
        public int jiraDoneCount;
        public List<Map<String, Object>> jiraDoneStories;
    }
}

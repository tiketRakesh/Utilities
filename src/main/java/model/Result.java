package model;

public class Result {
    public Sheet sheetResult;
    public Testrail testrailResult;
    public Jira jiraResult;

    public static class Sheet {
        public int totalRow;
        public int sheetShouldRunYCount;
        public int validTestIdsExtracted;
        public int sheetEnvironmentYCount;
    }

    public static class Testrail {
        public int automationStatusDoAgainCount;
        public int automationStatusDoneDoAgainCount;
    }

    public static class Jira {
        public int jiraDoneCount;
    }
}

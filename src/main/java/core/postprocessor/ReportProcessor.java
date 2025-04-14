package core.postprocessor;

import core.Config;
import model.Result;

public class ReportProcessor {
    private static Result RESULT = null;

    public ReportProcessor(Result result) {
        RESULT = result;
    }

    public void displayData() {
        System.out.println("==========     SUMMARY     ==========\n");
        System.out.println("Total JIRA 'Done' Count = " + RESULT.jiraResult.jiraDoneCount);
        System.out.println("Total Testrail 'Done/Do Again' Cases = " + RESULT.testrailResult.automationStatusDoneDoAgainCount);
        System.out.println("Total Testrail 'Do Again' Cases = " + RESULT.testrailResult.automationStatusDoAgainCount);
        System.out.println("Total Sheet Row = " + RESULT.sheetResult.totalRow);
        System.out.println("Total Sheet Row with 'ShouldRun = y' = " + RESULT.sheetResult.sheetShouldRunYCount);
        System.out.println("Total Sheet Row with '" + Config.ENVIRONMENT.getName() + " = y' = " + RESULT.sheetResult.sheetEnvironmentYCount);
        System.out.println("Total Sheet Row with Valid TestId Format = " + RESULT.sheetResult.validTestIdsExtracted);
        System.out.println("\n========== END OF SUMMARY  ==========");
    }

    public boolean verifyData() {
        System.out.println("\n==========    MATCHING    ==========\n");

        System.out.println("Total JIRA Done Count == Total Testrail 'Done/Do Again' Case");
        boolean check1 = RESULT.jiraResult.jiraDoneCount == RESULT.testrailResult.automationStatusDoneDoAgainCount;
        System.out.println("Check result = " + check1 + "\n");

        System.out.println("Total JIRA Done Count == Total Sheet Row with '" + Config.ENVIRONMENT.getName() + " = y'");
        boolean check2 = RESULT.jiraResult.jiraDoneCount == RESULT.sheetResult.sheetEnvironmentYCount;
        System.out.println("Check result = " + check2 + "\n");

        System.out.println("Total JIRA Done Count == (Total Testrail 'Do Again' Case) + (Total Sheet Row with 'ShouldRun = y') ");
        boolean check3 = RESULT.jiraResult.jiraDoneCount == RESULT.testrailResult.automationStatusDoAgainCount + RESULT.sheetResult.sheetShouldRunYCount;
        System.out.println("Check result = " + check3 + "\n");

        System.out.println("Total Sheet Row with 'ShouldRun = y' == (Total Testrail 'Done/Do Again' Cases) - (Total Testrail 'Do Again' Case)");
        boolean check4 = RESULT.sheetResult.sheetShouldRunYCount == RESULT.testrailResult.automationStatusDoneDoAgainCount - RESULT.testrailResult.automationStatusDoAgainCount;
        System.out.println("Check result = " + check4 + "\n");

        System.out.println("Total Sheet Row == Total Sheet Row with Valid TestId");
        boolean check5 = RESULT.sheetResult.totalRow == RESULT.sheetResult.validTestIdsExtracted;
        System.out.println("Check result = " + check5);

        System.out.println("\n========== END OF MATCHING ==========");

        return check1 && check2 && check3 && check4 & check5;
    }
}

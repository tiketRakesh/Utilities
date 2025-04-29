package core.postprocessor;

import core.Config;
import model.Result;
import org.json.JSONObject;

import java.util.*;

public class ReportProcessor {
    private static Result RESULT = null;

    public ReportProcessor(Result result) {
        RESULT = result;
    }

    static class CombinedData {
        String jiraId;
        String caseId;
        JSONObject testrailData;
        JSONObject jiraData;
        List<Object> sheetData;
    }

    public void displayData() {
        System.out.println("======================SUMMARY=====================");
        System.out.println("Total JIRA 'Done' Count = " + RESULT.jiraResult.jiraDoneCount);
        System.out.println("Total Testrail 'Done/Do Again' Cases = " + RESULT.testrailResult.automationStatusDoneDoAgainCount);
        System.out.println("Total Testrail 'Do Again' Cases = " + RESULT.testrailResult.automationStatusDoAgainCount);
        System.out.println("Total Sheet Row = " + RESULT.sheetResult.totalRow);
        System.out.println("Total Sheet Row with 'ShouldRun = y' = " + RESULT.sheetResult.sheetShouldRunYCount);
        System.out.println("Total Sheet Row with '" + Config.ENVIRONMENT.getName() + " = y' = " + RESULT.sheetResult.sheetEnvironmentYCount);
        System.out.println("Total Sheet Row with Valid TestId Format = " + RESULT.sheetResult.validTestIdsExtracted);
    }

    public boolean verifyData() {
        List<CombinedData> combinedDataList = new ArrayList<>();
        List<String> missingTestDataCaseId = new ArrayList<>();
        List<String> wrongTestDataCaseId = new ArrayList<>();

//        Map<String, JSONObject> jiraMap = new HashMap<>();
//        for (Map<String, Object> e : RESULT.jiraResult.jiraDoneStories) {
//            JSONObject jsonObject = new JSONObject(e);
//            jiraMap.put(jsonObject.getString("key"), jsonObject);
//        }

        List<Map<String, Object>> testrailList = new ArrayList<>(RESULT.testrailResult.groupedCasesData.get("all"));
        for (Map<String, Object> e : RESULT.testrailResult.groupedCasesData.get("all")) {
            JSONObject testrailData = new JSONObject(e);
//            List<String> testrailObject = Arrays.stream(testrailData.getString("refs").split(", ")).toList();

//            for (String s : testrailObject) {
//                if (s.contains("QAAUT")) {
//                    JSONObject jiraData = jiraMap.get(s);
//                    if (jiraData != null) {
            CombinedData combinedData = new CombinedData();
//                    combinedData.jiraId = s;
            combinedData.caseId = String.valueOf(testrailData.getInt("id"));
//                        combinedData.jiraData = jiraData;
            combinedData.testrailData = testrailData;
            combinedData.sheetData = RESULT.sheetResult.sheetData.get(combinedData.caseId);

            if (combinedData.sheetData == null) {
                missingTestDataCaseId.add(combinedData.caseId);
            } else {
                testrailList.remove(e);
                RESULT.sheetResult.sheetData.remove(combinedData.caseId);

                boolean check1 = combinedData.sheetData.get(Config.ENVIRONMENT_INDEX).toString().equalsIgnoreCase("y");
                boolean check2 = false;
                if (combinedData.testrailData.getInt("custom_automation_status") == 1) {
                    check2 = combinedData.sheetData.get(Config.SHOULD_RUN_INDEX).toString().equalsIgnoreCase("y");
                } else if (combinedData.testrailData.getInt("custom_automation_status") == 7) {
                    check2 = !combinedData.sheetData.get(Config.SHOULD_RUN_INDEX).toString().equalsIgnoreCase("y");
                }
                if (!(check1 && check2)) {
                    wrongTestDataCaseId.add(combinedData.caseId);
                }
            }
            combinedDataList.add(combinedData);

            switch (Config.ENVIRONMENT) {
                case PROD -> {
                    String jiraLabels = combinedData.jiraData.getJSONObject("fields").getString("labels").toLowerCase();
                    if (jiraLabels.contains("GK")) {
                        wrongTestDataCaseId.add(combinedData.caseId);
                    }
                }
            }
//                        jiraMap.remove(s);
//                    }
//                    break;
//                }
//            }
        }

//        System.out.println("=================LEFTOVER JIRA====================");
//        for (String jiraId: jiraMap.keySet()) {
//            System.out.println(jiraId);
//        }
//        System.out.println("===============LEFTOVER TESTRAIL==================");
//        for (Map<String, Object> testrailData: testrailList) {
//            JSONObject testrailDataJson = new JSONObject(testrailData);
//            System.out.println(testrailDataJson.getInt("id"));
//        }
//        System.out.println("=================LEFTOVER SHEET===================");
//        for (String key: RESULT.sheetResult.sheetData.keySet()) {
//            List<Object> row = RESULT.sheetResult.sheetData.get(key);
//            System.out.println(row.get(0).toString());
//        }
        System.out.println("================MISSING TESTDATA==================");
        for (String caseId: missingTestDataCaseId) {
            System.out.println(caseId);
        }
        System.out.println("=================WRONG TESTDATA===================");
        for (String caseId: wrongTestDataCaseId) {
            System.out.println(caseId);
        }
        System.out.println("=====================MATCHING=====================");

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

        System.out.println("===================END OF REPORT==================");

        return check1 && check2 && check3 && check4 && check5;
    }
}

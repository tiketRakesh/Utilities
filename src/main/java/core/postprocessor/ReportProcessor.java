package core.postprocessor;

import core.Config;
import model.Platform;
import model.Result;
import org.json.JSONObject;

import java.util.*;

public class ReportProcessor {
    public static Result RESULT = null;

    public ReportProcessor(Result result) {
        RESULT = result;
    }

    public static String getDetailedReport() {
        String text =
                "{" +
                    "\"type\":\"section\"," +
                    "\"text\":{" +
                        "\"type\":\"mrkdwn\"," +
                        "\"text\":\"" +
                            getLeftoverTestrail() +
                            getLeftoverSheet() +
                            getWrongTestdata() +
                    "\"}" +
                "}";

        return text;
    }

    private static String getLeftoverTestrail() {
        String text = "";

        if (!RESULT.testrailResult.testrailLeftover.isEmpty()) {
            text = "*Leftover Testrail* " +
                   "(_Testrail case id list which is not in matched with testdata sheet_)\\n" +
                   String.join(", ", RESULT.testrailResult.testrailLeftover) + "\\n";
        }

        return text;
    }

    private static String getLeftoverSheet() {
        String text = "";

        if (!RESULT.sheetResult.sheetLeftover.isEmpty()) {
            text = "*Leftover Sheet* " +
                   "(_Test method name list which is not in matched with testrail case id_)\\n" +
                   String.join(", ", RESULT.sheetResult.sheetLeftover) + "\\n";

        }

        return text;
    }

    private static String getMissingTestdata() {
        String text = "";

        if (!RESULT.sheetResult.missingTestData.isEmpty()) {
            text = "*Missing Testdata*\\n" + String.join(", ", RESULT.sheetResult.missingTestData) + "\\n";
        }

        return text;
    }

    private static String getWrongTestdata() {
        String text = "";

        if (!RESULT.sheetResult.wrongTestData.isEmpty()) {
            text = "*Wrong Testdata* " +
                   "(_Testrail case id list which is in automation status 'Do Again' but shouldRun still 'y' or for testdata which environment column is not 'y'_)\\n" +
                   String.join(", ", RESULT.sheetResult.wrongTestData) + "\\n";
        }

        return text;
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
        System.out.println("===============LEFTOVER TESTRAIL==================");
        System.out.println("Testrail case id list which is not in matched with testdata sheet");
        RESULT.testrailResult.testrailLeftover = new ArrayList<>();
        for (Map<String, Object> testrailData: testrailList) {
            JSONObject testrailDataJson = new JSONObject(testrailData);
            String caseId = String.valueOf(testrailDataJson.getInt("id"));
            System.out.println(caseId);
            RESULT.testrailResult.testrailLeftover.add(caseId);
        }
        System.out.println("=================LEFTOVER SHEET===================");
        System.out.println("Test method name list which is not in matched with testrail case id");
        RESULT.sheetResult.sheetLeftover = new ArrayList<>();
        for (String key: RESULT.sheetResult.sheetData.keySet()) {
            List<Object> row = RESULT.sheetResult.sheetData.get(key);
            String testMethodName = "";

            if (Config.PLATFORM.equals(Platform.APP)) {
                testMethodName += row.get(0).toString() + "-" + row.get(2).toString();
            }

            System.out.println(testMethodName);
            RESULT.sheetResult.sheetLeftover.add(testMethodName);
        }
//        System.out.println("================MISSING TESTDATA==================");
//        System.out.println("Testrail case id list which is not in testdata sheet");
//        RESULT.sheetResult.missingTestData = missingTestDataCaseId;
//        for (String caseId: missingTestDataCaseId) {
//            System.out.println(caseId);
//        }
        System.out.println("=================WRONG TESTDATA===================");
        System.out.println("Testrail case id list which is in automation status 'Do Again' but shouldRun still 'y' or for testdata which environment column is not 'y'");
        RESULT.sheetResult.wrongTestData = wrongTestDataCaseId;
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

        return check1 && check2 && check3 && check4 && check5 &&
                RESULT.testrailResult.testrailLeftover.isEmpty() &&
                RESULT.sheetResult.sheetLeftover.isEmpty() &&
                RESULT.sheetResult.wrongTestData.isEmpty();
    }
}

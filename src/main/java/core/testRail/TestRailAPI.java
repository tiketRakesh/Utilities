package core.testRail;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

public class TestRailAPI {

    private final String baseUrl;
    private final String authHeader;
    private final int limit = 250; // Default API limit per page

    public TestRailAPI(String baseUrl, String username, String apiKey) {
        this.baseUrl = baseUrl;
        this.authHeader = Base64.getEncoder().encodeToString((username + ":" + apiKey).getBytes());
    }

    public List<Map<String, Object>> fetchAndGroupTestCases(
            int projectId, int suiteId, List<Integer> sectionIds, int customAutomationType, List<Integer> customAutomationStatuses,int customTcStatus) {

        List<Map<String, Object>> allCases = new ArrayList<>();
        int offset = 0;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            boolean morePages = true;

            // Fetch all pages
            while (morePages) {
                String url = String.format("%s/index.php?/api/v2/get_cases/%d&suite_id=%d&offset=%d",
                        baseUrl, projectId, suiteId, offset);
                Response response = RestAssured.given()
                        .header("Authorization", "Basic " + authHeader)
                        .header("Content-Type", "application/json")
                        .get(url);

                JsonNode rootNode = objectMapper.readTree(response.getBody().asString());
                JsonNode cases = rootNode.get("cases");

                if (cases != null && cases.isArray()) {
                    // Convert each case to a Map and add to the list
                    cases.forEach(caseNode -> {
                        allCases.add(objectMapper.convertValue(caseNode, Map.class));
                    });

                    // Check if there are more pages
                    int fetchedSize = cases.size();
                    morePages = fetchedSize == limit; // If less than limit, no more pages
                } else {
                    morePages = false;
                }

                offset += limit; // Increment offset for the next page
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //do Again cases

        List<Map<String, Object>> doAgainCases= allCases.stream()
                .filter(testCase -> sectionIds.contains((Integer) testCase.get("section_id")))
                .filter(testCase -> testCase.get("custom_automation_type").equals(customAutomationType))
                .filter(testCase -> customAutomationStatuses.get(1).equals((Integer) testCase.get("custom_automation_status")))
                .filter(testCase -> testCase.get("custom_tcstatus").equals(customTcStatus))
                .collect(Collectors.toList());

        System.out.println("Do Again cases are ::::" + doAgainCases.size());
        // Filter cases based on input parameters
        return allCases.stream()
                .filter(testCase -> sectionIds.contains((Integer) testCase.get("section_id")))
                .filter(testCase -> testCase.get("custom_automation_type").equals(customAutomationType))
                .filter(testCase -> customAutomationStatuses.contains((Integer) testCase.get("custom_automation_status")))
                .filter(testCase -> testCase.get("custom_tcstatus").equals(customTcStatus))
                .collect(Collectors.toList());
    }
}



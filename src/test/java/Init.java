
import core.testRail.TestRailAPI;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Init {
    public static void main(String[] args) {
        String baseUrl = "https://tiket.testrail.com";
        String username = "";
        String apiKey = "";

        TestRailAPI testRailAPI = new TestRailAPI(baseUrl, username, apiKey);

        // Example parameters
        int projectId = 162;       //Project Name : Automation Regression
        int suiteId = 973;      // Gk Suite ID
        List<Integer> sectionIds = Arrays.asList(55315, 55314, 72167,72219); // List of section IDs for Flight Vertical
        int customAutomationType = 2; // 1--> No | 2--> Yes | 3--> TBR
        List<Integer> customAutomationStatus =  Arrays.asList(1,7); //1 --> Done   2---> Not Yet   7--> Do Again
        int customTcStatus = 3;
        // Fetch and group test cases
        List<Map<String, Object>> groupedCases = testRailAPI.fetchAndGroupTestCases(
                projectId, suiteId, sectionIds, customAutomationType, customAutomationStatus,customTcStatus);

        // Print grouped cases
        //groupedCases.forEach(System.out::println);

        //Print the number of test cases returned .
        System.out.println("No of test cases in the vertical are "+ groupedCases.size());

    }
}

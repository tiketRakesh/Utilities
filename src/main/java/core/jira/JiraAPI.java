package core.jira;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;


public class JiraAPI {

    private final String baseUrl;
    private final String authHeader;

    public JiraAPI() {
        this.baseUrl = "https://borobudur.atlassian.net";
        String apiKey = ""; // Replace with actual email:api_token
        this.authHeader = "Basic " + apiKey;
    }

    public List<Map<String, Object>> fetchDoneStories(String customFieldValue) {
        List<Map<String, Object>> doneStories = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        int startAt = 0;
        int maxResults = 50; // Adjust based on Jira API limit
        boolean hasMore = true;

        try {
            while (hasMore) {
                // JQL query (already URL-encoded)
                String jql = "project%20%3D%20QA-AUT%20AND%20type%20%3D%20Story%20AND%20labels%20NOT%20IN%20(ConvertNonLoginToLogin)" +
                        "%20AND%20%22Story%20Type%5BDropdown%5D%22%20!%3D%20Improvement" +
                        "%20AND%20cf%5B10006%5D%20%3D%20" + customFieldValue +
                        "%20AND%20status%20%3D%20Done";

                // Construct URL with pagination parameters
                String url = String.format("%s/rest/api/3/search?jql=%s&startAt=%d&maxResults=%d", baseUrl, jql, startAt, maxResults);

                // Send GET request with URL encoding disabled
                Response response = RestAssured.given()
                        .header("Authorization", authHeader)
                        .header("Content-Type", "application/json")
                        .header("Cookie", "atlassian.xsrf.token=0b59d70d41821da3dc8e430503bdb3cfaff22b1e_lin")
                        .urlEncodingEnabled(false) // Disable automatic encoding
                        .get(url);

                System.out.println("Rakesh : "+response.statusCode()+"::" +response.asString());

                // Parse response
                Map<String, Object> responseMap = objectMapper.readValue(response.getBody().asString(), Map.class);
                List<Map<String, Object>> issues = (List<Map<String, Object>>) responseMap.get("issues");

                if (issues != null && !issues.isEmpty()) {
                    doneStories.addAll(issues);
                }

                int total = (int) responseMap.get("total");
                startAt += maxResults;
                hasMore = startAt < total;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return doneStories;
    }
}
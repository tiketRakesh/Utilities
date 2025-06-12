package core.jira;
import core.Config;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;


public class JiraAPI {

    public final String baseUrl;
    public final String authHeader;

    public JiraAPI() {
        this.baseUrl = "https://borobudur.atlassian.net/rest/api/3/search";
        this.authHeader = Base64.getEncoder().encodeToString((Config.JIRA_USERNAME + ":" + Config.JIRA_API_KEY).getBytes());
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
                String jql = "project%20%3D%20QA-AUT%20AND%20type%20%3D%20Story" +
                        "%20AND%20%22Story%20Type%5BDropdown%5D%22%20!%3D%20Improvement" +
                        "%20AND%20cf%5B10006%5D%20%3D%20" + customFieldValue +
                        "%20AND%20status%20%3D%20Done";
                switch (Config.PLATFORM) {
                    case APP -> {
                        jql += "%20AND%20labels%20NOT%20IN%20(ConvertNonLoginToLogin)";
                    }
                }

                // Construct URL with pagination parameters
                String url = String.format("%s/rest/api/3/search?jql=%s&startAt=%d&maxResults=%d", baseUrl, jql, startAt, maxResults);

                // Send GET request with URL encoding disabled
                Response response = RestAssured.given()
                        .header("Authorization", "Basic " + authHeader)
                        .header("Content-Type", "application/json")
                        .header("Cookie", "atlassian.xsrf.token=45fabf3efd69db4bbd32e78b20e8baaf4072f2c2_lin")
                        .urlEncodingEnabled(false) // Disable automatic encoding
                        .get(url);

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
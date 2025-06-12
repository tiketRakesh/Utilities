package autothonTracker.jiraHelper;

import core.jira.JiraAPI;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class JiraHelper extends JiraAPI {


    public int getIssueCount(String jql) {
        System.out.println("JQL used is "+ jql);

        Response response =  RestAssured.given()
                .log().all() // Logs the full request URI
                .auth().preemptive().basic("rakesh.singh@tiket.com", "please read value from credntials file ")
                .header("Content-Type", "application/json")
                .accept("application/json")
                .queryParam("jql", jql)  // Use this instead of manual encoding
                .get(baseUrl);

        int statusCode = response.statusCode();
            System.out.println("response as "+ response.asString());
            if (statusCode == 200) {
                return response.then().extract().jsonPath().getInt("total");
            } else {
                System.err.println("Jira API call failed: " + response.asString());
                return 0;
            }
    }
}


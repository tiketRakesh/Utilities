import core.jira.JiraAPI;

import java.util.List;
import java.util.Map;

public class init_jira {
    public static void main(String[] args) {
        JiraAPI jiraAPI = new JiraAPI();
        List<Map<String, Object>> doneStories = jiraAPI.fetchDoneStories("QAAUT-453");

        // Print the number of fetched stories
        System.out.println("Total Done Stories: " + doneStories.size());
    }
}


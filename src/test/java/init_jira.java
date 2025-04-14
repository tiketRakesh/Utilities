import core.Config;
import core.jira.JiraAPI;
import model.Result;

import java.util.List;
import java.util.Map;

public class init_jira {
    public Result.Jira mainMethod() {
        Result.Jira jiraResult = new Result.Jira();

        JiraAPI jiraAPI = new JiraAPI();

        List<Map<String, Object>> doneStories = jiraAPI.fetchDoneStories(Config.JIRA_EPIC_ID);

        jiraResult.jiraDoneCount = doneStories.size();

        return jiraResult;
    }
}


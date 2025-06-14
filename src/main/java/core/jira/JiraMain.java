package core.jira;

import core.Config;
import core.jira.JiraAPI;
import model.testMappingValidator.Result;

import java.util.List;
import java.util.Map;

public class JiraMain {
    public Result.Jira mainMethod() {
        Result.Jira jiraResult = new Result.Jira();

        JiraAPI jiraAPI = new JiraAPI();

        List<Map<String, Object>> doneStories = jiraAPI.fetchDoneStories(Config.JIRA_EPIC_ID);

        jiraResult.jiraDoneCount = doneStories.size();
        jiraResult.jiraDoneStories = doneStories;

        return jiraResult;
    }
}


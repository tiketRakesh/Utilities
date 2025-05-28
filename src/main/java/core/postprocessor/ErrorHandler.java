package core.postprocessor;

import core.Config;

public class ErrorHandler {
    public static void handleCountMismatch(ReportProcessor reportProcessor) {
        String messagePayload = buildSlackMessage(reportProcessor);
        SlackProcessor.sendSlackNotification(messagePayload);
    }

    private static String buildSlackMessage(ReportProcessor reportProcessor) {
        return "{" +
                "\"blocks\":[" +
                    "{" +
                        "\"type\":\"section\"," +
                        "\"text\":{" +
                            "\"type\":\"mrkdwn\"," +
                            "\"text\":\"" + Config.PLATFORM + "-" + Config.VERTICAL + "-" + Config.ENVIRONMENT + " is having difference in count @here\"" +
                        "}," +
                        "\"accessory\":{" +
                            "\"type\":\"button\"," +
                            "\"text\":{" +
                                "\"type\":\"plain_text\"," +
                                "\"text\":\"Check\"," +
                                "\"emoji\":true" +
                            "}," +
                            "\"value\":\"JenkinsJob\"," +
                            "\"url\":\"https://selena.ggwp.red/job/Automation%20Count%20Checker/" + Config.JENKINS_BUILD_NUMBER + "/console\"," +
                            "\"action_id\":\"button-action\"" +
                        "}" +
                    "}," +
                    ReportProcessor.getDetailedReport() +
                "]" +
            "}";
    }
} 
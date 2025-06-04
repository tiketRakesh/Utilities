package core.postprocessor;

import core.io.PropertiesReader;
import lombok.SneakyThrows;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class SlackProcessor {
    private static final Properties PROPERTIES = PropertiesReader.read("src/main/resources/credential.properties");

    @SneakyThrows
    public static void sendSlackNotification(String payloadMessage) {
        URL url = new URL((String) PROPERTIES.getProperty("SLACK_WEBHOOK_URL"));
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoOutput(true);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Content-Type", "application/json");
        httpConn.setRequestProperty("Accept", "application/json");
        try (OutputStream os = httpConn.getOutputStream()) {
            byte[] input = payloadMessage.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        int responseCode = httpConn.getResponseCode();
        System.out.println("Response Code slack: " + responseCode);
    }
}
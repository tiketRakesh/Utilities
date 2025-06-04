package core.gSheet;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.auth.oauth2.ServiceAccountCredentials;


public class GSheetAPI {

    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final String CREDENTIALS_FILE_PATH = "/oauth2.json";


    public static Sheets getSheetsService() throws IOException {
        GoogleCredentials credentials;
        InputStream credentialsStream = GSheetAPI.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (credentialsStream == null) {
            throw new FileNotFoundException("Credentials file not found: " + CREDENTIALS_FILE_PATH);
        }

         credentials = ServiceAccountCredentials.fromStream(credentialsStream)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));


        return new Sheets.Builder(new com.google.api.client.http.javanet.NetHttpTransport(),
                new JacksonFactory(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static String extractTestId(String params) {
        if (params == null || params.isEmpty()) {
            return null;
        }

        // Regex to match testID: followed by digits
        Pattern pattern = Pattern.compile("testID:(\\d+)");
        Matcher matcher = pattern.matcher(params);

        if (matcher.find()) {
            try {
                Integer.parseInt(matcher.group(1));
                return matcher.group(1);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }
}

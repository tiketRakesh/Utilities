import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import core.gSheet.GSheetAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class init_gsheet {

    private static final String SPREADSHEET_ID = ""; // Replace with your spreadsheet ID
    private static final String RANGE = "Sheet1!A1:M"; // Replace with your sheet range
    public static void main(String[] args) throws IOException {
        // Initialize Google Sheets API client
        Sheets sheetsService = GSheetAPI.getSheetsService();

        // Fetch data from the sheet
        ValueRange response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, RANGE)
                .execute();

        List<List<Object>> rows = response.getValues();

        if (rows == null || rows.isEmpty()) {
            System.out.println("No data found in the spreadsheet.");
            return;
        }

        // Process rows and calculate results
        int shouldRunTrueCount = 0;
        List<String> testIds = new ArrayList<>();

        for (int i = 1; i < rows.size(); i++) { // Skip the header row
            List<Object> row = rows.get(i);

            // Check if ShouldRun flag is true
            if (row.size() > 3 && "y".equalsIgnoreCase(row.get(3).toString())) {
                shouldRunTrueCount++;

                // Extract testID from Staging-Params or Production-Params
                String stagingParams = row.size() > 11 ? row.get(11).toString() : "";
                String productionParams = row.size() > 12 ? row.get(12).toString() : "";

                String testId = GSheetAPI.extractTestId(stagingParams);
                if (testId == null) {
                    testId = GSheetAPI.extractTestId(productionParams);
                }

                if (testId != null) {
                    testIds.add(testId);
                }
            }
        }
        // Print results
        System.out.println("Number of rows with ShouldRun = true: " + shouldRunTrueCount);
        System.out.println("Extracted testIDs size " + testIds.size());
        System.out.println("Extracted testIDs: " + testIds);
    }
}

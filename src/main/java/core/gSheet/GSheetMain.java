package core.gSheet;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import core.Config;
import core.gSheet.GSheetAPI;
import model.testMappingValidator.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class GSheetMain {
    private static final String SPREADSHEET_ID = Config.SPREADSHEET_ID; // Replace with your spreadsheet ID
    private static final String RANGE = Config.SPREADSHEET_RANGE; // Replace with your sheet range
    private static final int SHOULD_RUN_INDEX = Config.SHOULD_RUN_INDEX;
    private static final int ENVIRONMENT_INDEX = Config.ENVIRONMENT_INDEX;
    private static final int TESTDATA_INDEX = Config.TESTDATA_INDEX;

    public Result.Sheet mainMethod() throws IOException {
        Result.Sheet sheetResult = new Result.Sheet();

        // Initialize Google Sheets API client
        Sheets sheetsService = GSheetAPI.getSheetsService();

        // Fetch data from the sheet
        ValueRange response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, RANGE)
                .execute();

        List<List<Object>> rows = response.getValues();

        if (rows == null || rows.isEmpty()) {
            System.out.println("No data found in the spreadsheet.");
            return sheetResult;
        }

        // Process rows and calculate results
        int shouldRunTrueCount = 0;
        int environmentTrueCount = 0;
        List<String> testIds = new ArrayList<>();

        int invalidTestIdsCount = 0;
        sheetResult.sheetData = new HashMap<>();
        for (int i = 1; i < rows.size(); i++) {
            List<Object> row = rows.get(i);

            if (row.size() > SHOULD_RUN_INDEX && row.get(SHOULD_RUN_INDEX).toString().equalsIgnoreCase("y")) {
                shouldRunTrueCount++;
            }

            if (row.size() > ENVIRONMENT_INDEX && row.get(ENVIRONMENT_INDEX).toString().equalsIgnoreCase("y")) {
                environmentTrueCount++;
            }

            String testdataParams = row.size() > TESTDATA_INDEX ? row.get(TESTDATA_INDEX).toString() : "";
            String testId = GSheetAPI.extractTestId(testdataParams);

            if (testId != null) {
                if (!testIds.contains(testId)) {
                    testIds.add(testId);
                    sheetResult.sheetData.put(testId, row);
                } else {
                    sheetResult.sheetData.put("duplicate" + invalidTestIdsCount, row);
                    invalidTestIdsCount++;
                }
            } else {
                sheetResult.sheetData.put("invalid" + invalidTestIdsCount, row);
                invalidTestIdsCount++;
            }
        }

        sheetResult.totalRow = rows.size() - 1; // -1 header row
        sheetResult.sheetShouldRunYCount = shouldRunTrueCount;
        sheetResult.sheetEnvironmentYCount = environmentTrueCount;
        sheetResult.validTestIdsExtracted = testIds.size();

        return sheetResult;
    }
}

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import core.Config;
import core.gSheet.GSheetAPI;
import model.Environment;
import model.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class init_gsheet {
    private static final String SPREADSHEET_ID = Config.SPREADSHEET_ID; // Replace with your spreadsheet ID
    private static final String RANGE = Config.SPREADSHEET_RANGE; // Replace with your sheet range
    private static final int SHOULD_RUN_INDEX = getShouldRunColumnIndex();
    private static final int ENVIRONMENT_INDEX = getEnvironmentColumnIndex();
    private static final int TESTDATA_INDEX = getTestdataColumnIndex();

    private static int getShouldRunColumnIndex() {
        switch (Config.PLATFORM) {
            case APP, DWEB, PANEL -> {
                return 3;
            }
            case API -> {
                return 6;
            }
            default -> {
                throw new NoSuchElementException("No such environment " + Config.PLATFORM);
            }
        }
    }

    private static int getEnvironmentColumnIndex() {
        int index = 0;

        switch (Config.PLATFORM) {
            case APP, DWEB, PANEL -> {
                index = 4;
                if (Config.ENVIRONMENT.equals(Environment.PREPROD)) index += 2;
                if (Config.ENVIRONMENT.equals(Environment.PROD)) index += 1;

                return index;
            }
            case API -> {
                index = 7;
                if (Config.ENVIRONMENT.equals(Environment.PREPROD)) index += 1;
                if (Config.ENVIRONMENT.equals(Environment.PROD)) index += 2;

                return index;
            }
            default -> {
                throw new NoSuchElementException("No such environment " + Config.PLATFORM);
            }
        }
    }

    private static int getTestdataColumnIndex() {
        int index = 0;

        switch (Config.PLATFORM) {
            case APP, DWEB, PANEL -> {
                index = 11;
                if (Config.ENVIRONMENT.equals(Environment.PREPROD)) index += 2;
                if (Config.ENVIRONMENT.equals(Environment.PROD)) index += 1;

                return index;
            }
            case API -> {
                index = 13;
                if (Config.ENVIRONMENT.equals(Environment.PREPROD)) index += 1;
                if (Config.ENVIRONMENT.equals(Environment.PROD)) index += 2;

                return index;
            }
            default -> {
                throw new NoSuchElementException("No such environment " + Config.PLATFORM);
            }
        }
    }

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
                testIds.add(testId);
            }
        }

        sheetResult.totalRow = rows.size() - 1; // -1 header row
        sheetResult.sheetShouldRunYCount = shouldRunTrueCount;
        sheetResult.sheetEnvironmentYCount = environmentTrueCount;
        sheetResult.validTestIdsExtracted = testIds.size();

        return sheetResult;
    }
}

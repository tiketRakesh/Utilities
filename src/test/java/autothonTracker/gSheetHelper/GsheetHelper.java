package autothonTracker.gSheetHelper;

import core.gSheet.GSheetAPI;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.util.*;

public class GsheetHelper extends GSheetAPI {

    public List<Map<String, String>> getTeamData(String spreadsheetId, String range) throws IOException {

        ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = response.getValues();

        List<Map<String, String>> teams = new ArrayList<>();
        int i =0 ;
        for (List<Object> row : values) {
            System.out.println("row "+ i + row);
            i++;
            if (row.size() >= 2) {
                Map<String, String> data = new HashMap<>();
                data.put("Team", row.get(0).toString());
                data.put("Vertical", row.get(1).toString());
                teams.add(data);
            }
        }
        return teams;
    }

    public void updateCell(String range, String value,String spreadsheetId) throws IOException {
        ValueRange body = new ValueRange().setValues(List.of(List.of(value)));
        sheetsService.spreadsheets().values().update(spreadsheetId, range, body)
                .setValueInputOption("RAW").execute();
    }

}

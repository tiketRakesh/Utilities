package autothonTracker;
import autothonTracker.gSheetHelper.GsheetHelper;
import autothonTracker.jiraHelper.JiraHelper;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;

public class AutoThonTrackerMain {

    private static final Map<String, Map<String, String>> verticalParentMap = Map.of(
            "Accom", Map.of("Panel", "QAAUT-29970", "dWeb", "QAAUT-29983", "mWeb", "QAAUT-29984"),
            "Pricing", Map.of("Panel", "QAAUT-29972", "dWeb", "QAAUT-30030", "mWeb", "QAAUT-30031"),
            "B2B", Map.of("Panel", "QAAUT-29971", "dWeb", "QAAUT-30008", "mWeb", "QAAUT-30010")
            // Add other verticals and platform-specific parent IDs as needed
    );

    public static void main(String[] args) throws IOException {

        String sheetId = "1ZNxMK1fZu_wBfrVMN671nh5DvirZ_8Wcx_ACoshfN9E";//System.getenv("SHEET_ID");
        GsheetHelper gsheetHelper = new GsheetHelper();
        JiraHelper jiraHelper = new JiraHelper();
        List<Map<String, String>> teamDataList = gsheetHelper.getTeamData(sheetId,"B4:C");
        int startRow = 4;

        for (int i = 0; i < teamDataList.size(); i++) {
            Map<String, String> teamData = teamDataList.get(i);
            String team = teamData.get("Team");
            String vertical = teamData.get("Vertical");
            int row = startRow + i;

            for (String platform : List.of("Panel", "dWeb", "mWeb")) {
                for (String complexity : List.of("NonComplex", "Complex")) {
                    for (String status : List.of("WIP", "Done")) {
                        String[] verticals = vertical.split("_");
                        List<String> parentIds = new ArrayList<>();

                        for (String v : verticals) {
                            Map<String, String> platformParents = verticalParentMap.getOrDefault(v, Map.of());
                            String parentId = platformParents.get(platform);
                            if (parentId != null) parentIds.add(parentId);
                        }
                        if (parentIds.isEmpty()) continue;

                        String parentClause = "parent in (" + String.join(", ", parentIds) + ")";
                        String jqlStatusClause = status.equals("Done")
                                ? "status in (\"Done\")"
                                : "status not in (\"Done\", \"Todo\", \"Invalid\", \"Blocked\", \"Dropped\")";

                        String jql = String.format(
                                "project=QA-AUT AND %s AND labels=\"%s\" AND labels=\"%s\" AND %s ",
                                parentClause, complexity, team, jqlStatusClause
                        );

                        int count = jiraHelper.getIssueCount(jql);
                        String col = getColumnLetter(platform, complexity, status);
                        String cellRef = col + row;
                        gsheetHelper.updateCell(cellRef, String.valueOf(count),sheetId);
                    }
                }
            }
        }

    }

    private static String getColumnLetter(String platform, String complexity, String status) {
        Map<String, String> map = new HashMap<>();
        map.put("Panel-NonComplex-WIP", "F");
        map.put("Panel-NonComplex-Done", "G");
        map.put("Panel-Complex-WIP", "I");
        map.put("Panel-Complex-Done", "J");
        map.put("dWeb-NonComplex-WIP", "M");
        map.put("dWeb-NonComplex-Done", "N");
        map.put("dWeb-Complex-WIP", "P");
        map.put("dWeb-Complex-Done", "Q");
        map.put("mWeb-NonComplex-WIP", "T");
        map.put("mWeb-NonComplex-Done", "U");
        map.put("mWeb-Complex-WIP", "W");
        map.put("mWeb-Complex-Done", "X");
        return map.getOrDefault(platform + "-" + complexity + "-" + status, "ZZ");
    }

}

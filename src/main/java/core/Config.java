package core;

import core.io.PropertiesReader;
import model.Environment;
import model.Platform;
import model.Vertical;

import java.util.*;

public class Config {
    public static final Properties CONFIG_PROPERTIES = PropertiesReader.read("src/main/resources/config.properties");
    public static final Properties CREDENTIALS_PROPERTIES = PropertiesReader.read("src/main/resources/credential.properties");
    public static final String TESTRAIL_USERNAME = CREDENTIALS_PROPERTIES.getProperty("TESTRAIL_USERNAME");
    public static final String TESTRAIL_PASSWORD = CREDENTIALS_PROPERTIES.getProperty("TESTRAIL_PASSWORD");
    public static final String JIRA_USERNAME = CREDENTIALS_PROPERTIES.getProperty("JIRA_USERNAME");
    public static final String JIRA_API_KEY = CREDENTIALS_PROPERTIES.getProperty("JIRA_API_KEY");
    public static final Platform PLATFORM = Platform.valueOf(System.getProperty("platform").toUpperCase());
    public static final Vertical VERTICAL = Vertical.valueOf(System.getProperty("vertical").toUpperCase());
    public static final Environment ENVIRONMENT = Environment.valueOf(System.getProperty("environment").toUpperCase());
    public static final String SPREADSHEET_ID = getSheetId();
    public static final String SPREADSHEET_RANGE = getSheetRange();
    public static final int TESTRAIL_PROJECT_ID = Integer.parseInt(CONFIG_PROPERTIES.getProperty("TESTRAIL_PROJECT_ID"));
    public static final int TESTRAIL_SUITE_ID = getSuiteId();
    public static final String JENKINS_BUILD_NUMBER = CREDENTIALS_PROPERTIES.getProperty("JENKINS_JOB_NUMBER");
    public static List<Integer> TESTRAIL_SECTION_IDs = getSectionId();
    public static final String JIRA_EPIC_ID = getEpicId();
    public static final int SHOULD_RUN_INDEX = getShouldRunColumnIndex();
    public static final int ENVIRONMENT_INDEX = getEnvironmentColumnIndex();
    public static final int TESTDATA_INDEX = getTestdataColumnIndex();

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

    private static List<Integer> getSectionId() {
        List<Integer> result = new ArrayList<>();

        String propertiesName = PLATFORM.getName() + "_" + VERTICAL.getName() + "_" + "SECTION_ID";
        Arrays.stream(CONFIG_PROPERTIES.getProperty(propertiesName).split(",")).toList().forEach(sectionId -> result.add(Integer.parseInt(sectionId)));

        return result;
    }

    private static int getSuiteId() {
        String propertiesName = PLATFORM.getName() + "_" + "SUITE_ID";

        return Integer.parseInt(CONFIG_PROPERTIES.getProperty(propertiesName));
    }

    private static String getSheetId() {
        String propertiesName = PLATFORM.getName() + "_" + VERTICAL.getName() + "_" + "SHEET_ID";

        return CONFIG_PROPERTIES.getProperty(propertiesName);
    }

    private static String getSheetRange() {
        switch (PLATFORM) {
            case APP, DWEB, PANEL -> {return "Sheet1!A1:N";}
            case API -> {return "Sheet1!A1:X";}
            default -> throw new NoSuchElementException("There is no platform type '"+PLATFORM+"'");
        }
    }

    private static String getEpicId() {
        String propertiesName = PLATFORM.getName() + "_" + VERTICAL.getName() + "_" + "EPIC_ID";

        return CONFIG_PROPERTIES.getProperty(propertiesName);
    }
}

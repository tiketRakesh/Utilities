import core.Config;
import core.postprocessor.ReportProcessor;
import core.postprocessor.ErrorHandler;
import core.util.RetryUtil;
import model.testMappingValidator.Result;
import core.testRail.TestRailMain;
import core.gSheet.GSheetMain;
import core.jira.JiraMain;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TestMappingValidator {
    private static final Logger logger = LoggerFactory.getLogger(TestMappingValidator.class);

    @Test
    public void validateTestCountsAcrossSystems() throws Exception {
        logger.info("Starting test count validation across systems");
        
        // Initialize result container
        Result result = new Result();

        // Collect data from TestRail with retry
        logger.info("Collecting data from TestRail");
        result.testrailResult = RetryUtil.retry(() -> {
            TestRailMain initTestrail = new TestRailMain();
            return initTestrail.mainMethod();
        });
        logger.debug("TestRail data collected: {}", result.testrailResult);

        // Collect data from Google Sheets with retry
        logger.info("Collecting data from Google Sheets");
        result.sheetResult = RetryUtil.retry(() -> {
            GSheetMain initGsheet = new GSheetMain();
            return initGsheet.mainMethod();
        });
        logger.debug("Google Sheets data collected: {}", result.sheetResult);

        // Collect data from Jira with retry
        logger.info("Collecting data from Jira");
        result.jiraResult = RetryUtil.retry(() -> {
            JiraMain initJira = new JiraMain();
            return initJira.mainMethod();
        });
        logger.debug("Jira data collected: {}", result.jiraResult);

        // Process and validate the results
        logger.info("Processing and validating results");
        ReportProcessor reportProcessor = new ReportProcessor(result);
        reportProcessor.displayData();
        boolean checkResult = reportProcessor.verifyData();

        try {
            Assert.assertTrue("Test counts should match across all systems", checkResult);
            logger.info("Validation successful: All test counts match");
        } catch (AssertionError e) {
            logger.error("Validation failed: Test counts do not match across systems", e);
            ErrorHandler.handleCountMismatch(reportProcessor);
            Assert.fail("The count did not match across systems");
        }
    }
} 
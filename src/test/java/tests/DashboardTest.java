package tests;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import base.BaseTest;
import pages.DashboardPage;
import utils.LoggerFactory;

public class DashboardTest extends BaseTest {

//	private WebDriver driver; // Assuming driver is initialized properly elsewhere in your test framework
//	private static final Logger log = LogManager.getLogger(DashboardTest.class); // Logger for this test class
	private static final Logger log = LoggerFactory.getLogger(DashboardTest.class);

	@BeforeClass
	public void setUp() {
		// Initialize your WebDriver here
		// Example: driver = new ChromeDriver();
		log.info("WebDriver initialized for Dashboard Test.");
	}

	@Test(description = "Test Dashboard Page Logging")
	public void testDashboardLogging() {
		ThreadContext.put("logFolder", "pages");
		ThreadContext.put("className", "DashboardPage");

		DashboardPage dashboard = new DashboardPage(driver);
		dashboard.navigateToPatientPage();

		// Check if the log file is created
		File logFile = new File("logs/pages/DashboardPage.log"); // Adjust the path based on your configuration

		// Validate the log file creation
		Assert.assertTrue(logFile.exists(), "Log file for DashboardPage should exist.");

		// Log output for verification
		if (logFile.exists()) {
			log.info("Log file created successfully: " + logFile.getAbsolutePath());
		} else {
			log.error("Failed to create log file.");
		}

		// Clear ThreadContext after logging
		ThreadContext.clearAll();
	}

}
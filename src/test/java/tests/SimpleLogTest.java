package tests;
import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SimpleLogTest {

	private static final Logger log = LogManager.getLogger(SimpleLogTest.class);

	@Test(description = "Test log file creation")
	public void simpleLogTests() {
		ThreadContext.put("logFolder", "pages");
		ThreadContext.put("className", "TestLogging");

		log.info("This is a test log message.");

		File logFile = new File("logs/pages/TestLogging.log");
		if (logFile.exists()) {
			log.info("Log file exists: " + logFile.getAbsolutePath());
		} else {
			log.error("Log file does not exist: " + logFile.getAbsolutePath());
		}

		// Assertion to check log file presence
		Assert.assertTrue(logFile.exists(), "Log file should exist.");
	}
}
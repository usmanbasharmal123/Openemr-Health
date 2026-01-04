package listeners;

import java.lang.reflect.Field;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import utils.AllureUtils;
import utils.LoggerFactory;
import utils.ScreenshotUtil;

public class AllureListener implements ITestListener {

	private static final Logger log = LoggerFactory.getLogger(AllureListener.class);

	@Override
	public void onStart(ITestContext context) {
		log.info("TestNG onStart: {}", context.getName());
	}

	@Override
	public void onFinish(ITestContext context) {
		log.info("TestNG onFinish: {}", context.getName());
	}

	@Override
	public void onTestStart(ITestResult result) {
		log.info("Test Started: {}", result.getMethod().getMethodName());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		log.info("Test Passed: {}", result.getMethod().getMethodName());
	}

	@Override
	public void onTestFailure(ITestResult result) {
		log.error("Test Failed: {}", result.getMethod().getMethodName(), result.getThrowable());

		try {
			Object testInstance = result.getInstance();

			// BaseTest contains the protected WebDriver driver
			Field driverField = testInstance.getClass().getSuperclass().getDeclaredField("driver");
			driverField.setAccessible(true);

			WebDriver driver = (WebDriver) driverField.get(testInstance);

			if (driver != null) {

				// Save screenshot for email thumbnails
				ScreenshotUtil.captureScreenshot(driver, result.getMethod().getMethodName());

				// Attach Allure artifacts
				AllureUtils.takeScreenshot(driver);
				AllureUtils.attachBrowserConsoleLogs(driver);
				AllureUtils.attachPageSource(driver);
				AllureUtils.attachNetworkLogs(driver);

			} else {
				log.error("Driver is NULL — cannot capture logs.");
			}

		} catch (Exception e) {
			log.error("Unable to capture logs via reflection: {}", e.getMessage());
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		log.warn("Test Skipped: {}", result.getMethod().getMethodName());
	}
}

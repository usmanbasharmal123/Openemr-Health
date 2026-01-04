package base;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import driver.DriverFactory;
import pages.DashboardPage;
import pages.LoginPage;
import utils.LoggerFactory;
import utils.ScreenshotUtil;

public abstract class BaseTest {

	protected WebDriver driver;

	private static Logger baseLogger;
	protected Logger log;

	private DriverFactory driverFactory;

	@BeforeSuite(alwaysRun = true)
	public void beforeSuite() {

		ThreadContext.put("logFolder", "base");
		ThreadContext.put("className", "BaseTest");

		baseLogger = LogManager.getLogger(BaseTest.class);
		baseLogger.info("===== Test Suite Execution Started =====");
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp(Method method) {

		log = LoggerFactory.getLogger(method.getDeclaringClass());

		baseLogger.info("BaseTest.setUp() called for test class: {}", method.getDeclaringClass().getSimpleName());
		log.info("----- Test Setup Started -----");

		driverFactory = new DriverFactory();
		driver = driverFactory.initDriver();

		// Make driver available to ExtentListener
		Reporter.getCurrentTestResult().getTestContext().setAttribute("driver", driver);

		baseLogger.info("Driver initialized in BaseTest");
		log.info("Driver initialized in BaseTest");
	}

	public DashboardPage loginToOpenEMR() {
		LoginPage login = new LoginPage(driver);
		return login.enterUsername("XET-admin-88").enterPassword("B@omar.1234567").clickLogin();
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown(ITestResult result) {

		String methodName = result.getMethod().getMethodName();

		baseLogger.info("BaseTest.tearDown() called for method: {}", methodName);

		switch (result.getStatus()) {
		case ITestResult.FAILURE:
			log.error("Test FAILED: {}", methodName, result.getThrowable());
			String screenshotPath = ScreenshotUtil.captureScreenshot(driver, methodName);
			log.info("Screenshot captured at: {}", screenshotPath);
			break;

		case ITestResult.SUCCESS:
			log.info("Test PASSED: {}", methodName);
			break;

		case ITestResult.SKIP:
			log.warn("Test SKIPPED: {}", methodName);
			break;
		}

		driverFactory.quitDriver();
		log.info("----- Test Teardown Completed -----");
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite() {
		baseLogger.info("===== Test Suite Execution Completed =====");
	}
}

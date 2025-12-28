package listeners;

import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import utils.LoggerFactory;

public class TestListener implements ITestListener {

	private static final Logger log = LoggerFactory.getLogger(TestListener.class);

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
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		log.warn("Test Skipped: {}", result.getMethod().getMethodName());
	}
}

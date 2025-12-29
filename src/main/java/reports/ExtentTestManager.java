package reports;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {

	// Thread-safe map for parallel execution
	private static final Map<Long, ExtentTest> testMap = new ConcurrentHashMap<>();

	/**
	 * Returns the ExtentTest instance for the current thread. Prevents
	 * NullPointerException by returning a fallback test if missing.
	 */
	public static synchronized ExtentTest getTest() {
		ExtentTest test = testMap.get(Thread.currentThread().getId());

		if (test == null) {
			// Safety fallback: create a placeholder test to avoid crashes
			test = ExtentManager.getInstance().createTest("UndefinedTest-" + Thread.currentThread().getId());
			testMap.put(Thread.currentThread().getId(), test);
		}

		return test;
	}

	/**
	 * Creates a new test entry for the current thread.
	 */
	public static synchronized void startTest(String testName) {
		ExtentTest test = ExtentManager.getInstance().createTest(testName);
		testMap.put(Thread.currentThread().getId(), test);
	}

	/**
	 * Logs a step safely.
	 */
	public static void logStep(String message) {
		getTest().info(message);
	}
}

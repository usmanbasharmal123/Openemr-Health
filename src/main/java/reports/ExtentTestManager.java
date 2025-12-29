package reports;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {

	private static Map<Long, ExtentTest> testMap = new ConcurrentHashMap<>();

	public static synchronized ExtentTest getTest() {
		return testMap.get(Thread.currentThread().getId());
	}

	public static synchronized void startTest(String testName) {
		ExtentTest test = ExtentManager.getInstance().createTest(testName);
		testMap.put(Thread.currentThread().getId(), test);
	}

	public static void logStep(String message) {
		getTest().info(message);
	}
}

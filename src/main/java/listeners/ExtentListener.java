package listeners;

import java.io.File;
import java.io.IOException;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.Status;

import reports.ExtentManager;
import reports.ExtentTestManager;
import utils.ConfigReader;
import utils.InlineExtentReport;
import utils.ScreenshotUtil;

public class ExtentListener implements ITestListener {

	@Override
	public void onStart(ITestContext context) {

		// Initialize ExtentReports
		ExtentManager.getInstance();

		// Add system info (browser, OS)
		ExtentManager.getInstance().setSystemInfo("Browser", ConfigReader.get("browser"));
		ExtentManager.getInstance().setSystemInfo("OS", System.getProperty("os.name"));
	}

	@Override
	public void onTestStart(ITestResult result) {

		ExtentTestManager.startTest(result.getMethod().getMethodName());

		// Add test description
		String description = result.getMethod().getDescription();
		if (description != null && !description.isEmpty()) {
			ExtentTestManager.getTest().info("<b>Description:</b> " + description);
		}

		// Add categories (Smoke, Regression, Sanity)
		if (result.getMethod().getGroups().length > 0) {
			for (String group : result.getMethod().getGroups()) {
				ExtentTestManager.getTest().assignCategory(group);
			}
		}

		// Add author
		if (description != null && description.contains("Author:")) {
			String author = description.replace("Author:", "").trim();
			ExtentTestManager.getTest().assignAuthor(author);
		} else {
			ExtentTestManager.getTest().assignAuthor("Usman");
		}
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		ExtentTestManager.getTest().log(Status.PASS, "Test Passed");
	}

	@Override
	public void onTestFailure(ITestResult result) {

		ExtentTestManager.getTest().log(Status.FAIL, result.getThrowable());

		String screenshotPath = ScreenshotUtil.captureScreenshot(
				(org.openqa.selenium.WebDriver) result.getTestContext().getAttribute("driver"),
				result.getMethod().getMethodName());

		// Add screenshot with correct relative path
		try {
			ExtentTestManager.getTest().addScreenCaptureFromPath(screenshotPath, "Screenshot");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		ExtentTestManager.getTest().log(Status.SKIP, "Test Skipped");
	}

	@Override
	public void onFinish(ITestContext context) {

		// Dashboard summary
		int passed = context.getPassedTests().size();
		int failed = context.getFailedTests().size();
		int skipped = context.getSkippedTests().size();
		int total = passed + failed + skipped;

		ExtentManager.getInstance().setSystemInfo("Total Tests", String.valueOf(total));
		ExtentManager.getInstance().setSystemInfo("Passed", String.valueOf(passed));
		ExtentManager.getInstance().setSystemInfo("Failed", String.valueOf(failed));
		ExtentManager.getInstance().setSystemInfo("Skipped", String.valueOf(skipped));

		// Flush report
		ExtentManager.getInstance().flush();

		// Auto-open the latest report
		try {
			File dir = new File("reports");

			File[] files = dir.listFiles((d, name) -> name.endsWith(".html"));

			if (files != null && files.length > 0) {

				File latestReport = files[0];

				for (File f : files) {
					if (f.lastModified() > latestReport.lastModified()) {
						latestReport = f;
					}
				}

				// Open in default browser
				java.awt.Desktop.getDesktop().browse(latestReport.toURI());
			}

		} catch (Exception e) {
			System.out.println("Failed to auto-open ExtentReport: " + e.getMessage());
		}
		// Paths to your Extent files

		InlineExtentReport.inlineResources("reports/ExtentReport.html");
	}

}

package utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotUtil {

	private static final Logger frameworkLog = LogManager.getLogger("FrameworkLogger");

	private ScreenshotUtil() {
	}

	public static String captureScreenshot(WebDriver driver, String testName) {

		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

		// Save screenshot INSIDE reports folder
		String screenshotPath = "reports/screenshots/" + testName + "_" + timestamp + ".png";

		File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		File destFile = new File(screenshotPath);

		try {
			// Create folder if missing
			destFile.getParentFile().mkdirs();

			FileUtils.copyFile(srcFile, destFile);
			frameworkLog.info("Screenshot saved to {}", screenshotPath);
		} catch (IOException e) {
			frameworkLog.error("Failed to save screenshot", e);
		}

		return "screenshots/" + testName + "_" + timestamp + ".png";

	}
}

package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import io.qameta.allure.Allure;

public class AllureUtils {

	public static void takeScreenshot(WebDriver driver) {
		try {
			byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

			Allure.getLifecycle().addAttachment("Screenshot", "image/png", "png", screenshot);

		} catch (Exception e) {
			System.out.println("Failed to attach screenshot to Allure: " + e.getMessage());
		}
	}

}

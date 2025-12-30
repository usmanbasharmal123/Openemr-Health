package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import io.qameta.allure.Allure;

public class AllureUtils {

	public static void takeScreenshot(WebDriver driver) {
		Allure.getLifecycle().addAttachment("Screenshot", "image/png", "png",
				((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
	}
}

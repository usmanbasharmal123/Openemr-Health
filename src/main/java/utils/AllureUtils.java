package utils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v127.network.Network;
import org.openqa.selenium.logging.LogEntries;

import io.qameta.allure.Allure;

public class AllureUtils {

	// Screenshot
	public static void takeScreenshot(WebDriver driver) {
		try {
			byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
			Allure.addAttachment("Screenshot", new ByteArrayInputStream(screenshot));
		} catch (Exception e) {
			System.out.println("Failed to attach screenshot: " + e.getMessage());
		}
	}

	// Browser console logs
	public static void attachBrowserConsoleLogs(WebDriver driver) {
		try {
			LogEntries logs = driver.manage().logs().get("browser");
			StringBuilder sb = new StringBuilder();

			logs.forEach(entry -> sb.append(entry.getLevel()).append(": ").append(entry.getMessage()).append("\n"));

			Allure.addAttachment("Browser Console Logs", sb.toString());
		} catch (Exception e) {
			System.out.println("Failed to attach console logs: " + e.getMessage());
		}
	}

	// HTML page source
	public static void attachPageSource(WebDriver driver) {
		try {
			String pageSource = driver.getPageSource();
			Allure.addAttachment("HTML Page Source", "text/html", pageSource, ".html");
		} catch (Exception e) {
			System.out.println("Failed to attach page source: " + e.getMessage());
		}
	}

	// Network logs (Selenium DevTools)
	public static void attachNetworkLogs(WebDriver driver) {
		try {
			if (!(driver instanceof ChromeDriver)) {
				Allure.addAttachment("Network Logs", "Network logs only supported on ChromeDriver");
				return;
			}

			ChromeDriver chromeDriver = (ChromeDriver) driver;
			DevTools devTools = chromeDriver.getDevTools();
			devTools.createSession();

			// Enable network tracking
			devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

			List<String> networkEvents = new ArrayList<>();

			// Capture requests
			devTools.addListener(Network.requestWillBeSent(), event -> {
				networkEvents.add("REQUEST: " + event.getRequest().getUrl());
			});

			// Capture responses
			devTools.addListener(Network.responseReceived(), event -> {
				networkEvents.add(
						"RESPONSE: " + event.getResponse().getUrl() + " | Status: " + event.getResponse().getStatus());
			});

			// Attach collected logs
			Allure.addAttachment("Network Logs", String.join("\n", networkEvents));

		} catch (Exception e) {
			System.out.println("Failed to attach network logs: " + e.getMessage());
		}
	}
}

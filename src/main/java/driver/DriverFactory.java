package driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.ConfigReader;
import utils.LoggerFactory;

public class DriverFactory {

	private WebDriver driver;

	// Child logger (driver/DriverFactory.log)
	private final Logger log;

	// Framework-level logger
	private static final Logger frameworkLog = LogManager.getLogger("FrameworkLogger");

	public DriverFactory() {
		this.log = LoggerFactory.getLogger(getClass());
	}

	public WebDriver initDriver() {

		if (driver == null) {
			String browser = ConfigReader.get("browser");

			// Log to both framework + driver logs
			frameworkLog.info("Initializing browser: {}", browser);
			log.info("Initializing browser: {}", browser);

			if ("chrome".equalsIgnoreCase(browser)) {

				WebDriverManager.chromedriver().setup();

				ChromeOptions options = new ChromeOptions();
				options.addArguments("--start-maximized");
				options.addArguments("--remote-allow-origins=*");
				options.addArguments("--disable-notifications");
				options.addArguments("--disable-infobars");

				driver = new ChromeDriver(options);

				frameworkLog.info("ChromeDriver initialized successfully");
				log.info("ChromeDriver initialized successfully");

			} else {
				frameworkLog.error("Unsupported browser: {}", browser);
				log.error("Unsupported browser: {}", browser);
				throw new IllegalArgumentException("Unsupported browser: " + browser);
			}

			String url = ConfigReader.get("url");

			frameworkLog.info("Navigating to URL: {}", url);
			log.info("Navigating to URL: {}", url);

			driver.get(url);
		}

		return driver;
	}

	public void quitDriver() {
		if (driver != null) {
			frameworkLog.info("Quitting browser session");
			log.info("Quitting browser session");

			driver.quit();
			driver = null;
		}
	}
}

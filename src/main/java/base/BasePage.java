package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import utils.LoggerFactory;
import utils.WaitHelper;

public abstract class BasePage {

	protected WebDriver driver;

	// Dedicated BasePage logger
	private final Logger baseLogger;

	// Child page logger (LoginPage, DashboardPage, etc.)
	protected Logger log;

	protected BasePage(WebDriver driver) {
		this.driver = driver;

		// BasePage logger (writes to BasePage.log)
		this.baseLogger = LogManager.getLogger("BasePageLogger");
		baseLogger.info("BasePage constructor started for child: {}", getClass().getSimpleName());

		// Child logger
		this.log = LoggerFactory.getLogger(getClass());
		log.info("Page object created: {}", getClass().getSimpleName());

		baseLogger.info("BasePage constructor completed for child: {}", getClass().getSimpleName());

		PageFactory.initElements(driver, this);
	}

	protected void click(WebElement element, String elementName) {
		baseLogger.info("BasePage.click() called for element: {}", elementName);
		log.info("[BasePage.click] Clicking on element: {}", elementName);
		WaitHelper.waitForElementToBeClickable(driver, element);
		element.click();
	}

	protected void type(WebElement element, String text, String elementName) {
		baseLogger.info("BasePage.type() called for element: {}", elementName);
		log.info("[BasePage.type] Typing '{}' into element: {}", text, elementName);
		WaitHelper.waitForElementToBeVisible(driver, element);
		element.clear();
		element.sendKeys(text);
	}

	protected String getText(WebElement element, String elementName) {
		baseLogger.info("BasePage.getText() called for element: {}", elementName);
		WaitHelper.waitForElementToBeVisible(driver, element);
		String text = element.getText();
		log.info("[BasePage.getText] Captured text '{}' from element: {}", text, elementName);
		return text;
	}

	public String getPageTitle() {
		baseLogger.info("BasePage.getPageTitle() called");
		String title = driver.getTitle();
		log.info("[BasePage.getPageTitle] Current page title: {}", title);
		return title;
	}
}

package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

public class PatientPage extends BasePage {

	// Example element — update with your real locators
	@FindBy(xpath = "//h2[contains(text(),'Patient')]")
	private WebElement patientHeader;

	public PatientPage(WebDriver driver) {
		super(driver);
		log.info("PatientPage initialized");
	}

	public String getHeaderText() {
		return getText(patientHeader, "Patient Page Header");
	}
}

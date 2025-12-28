package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

public class DashboardPage extends BasePage {

	// Updated locator for Patient menu
	@FindBy(xpath = "//div[contains(@class,'oe-dropdown-toggle') and contains(text(),'Patient')]")
	private WebElement patientMenu;

	// Optional: If submenu exists
	@FindBy(xpath = "//a[contains(text(),'Patients') or contains(text(),'New/Search')]")
	private WebElement patientSubMenu;

	public DashboardPage(WebDriver driver) {
		super(driver);
		log.info("DashboardPage initialized");
	}

	public PatientPage navigateToPatientPage() {
		log.info("Clicking Patient main menu");
		click(patientMenu, "Patient Menu");

		// Some OpenEMR versions require clicking submenu
		try {
			click(patientSubMenu, "Patient Sub Menu");
		} catch (Exception e) {
			log.warn("Submenu not found or not required");
		}

		log.info("Navigated to Patient Page from Dashboard");
		return new PatientPage(driver);
	}
}

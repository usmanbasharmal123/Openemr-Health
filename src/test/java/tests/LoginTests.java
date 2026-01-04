package tests;

import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import pages.DashboardPage;
import pages.LoginPage;
import utils.LoggerFactory;

@Epic("Authentication")
@Feature("Login Module")
public class LoginTests extends BaseTest {
	private static final Logger log = LoggerFactory.getLogger(LoginTests.class);

	@Story("Valid login with correct credentials")
	@Severity(SeverityLevel.CRITICAL)
	@Test(description = "Valid login to OpenEMR")
	public void validLoginTest() {
		log.info("=== Starting test: validLoginTest ===");

		LoginPage loginPage = new LoginPage(driver);
		DashboardPage dashboardPage = loginPage.enterUsername("XET-admin-88").enterPassword("B@omar.1234567")
				.clickLogin();

		String title = dashboardPage.getPageTitle();
		log.info("Verifying dashboard title: {}", title);
		System.out.println(System.getProperty("log4j.configurationFile"));

		Assert.assertTrue(title.contains("OpenEMR"), "Dashboard title does not contain 'OpenEMR'");
		log.info("=== Completed test: validLoginTest ===");
	}

	@Story("Intentional failure to test screenshot")
	@Severity(SeverityLevel.NORMAL)
	@Test(description = "Intentional failure to test Allure screenshot")
	public void failTest() {
		Assert.fail("Intentional failure to test Allure screenshot");
	}
}

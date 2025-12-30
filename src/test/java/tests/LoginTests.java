package tests;

import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import pages.DashboardPage;
import pages.LoginPage;
import utils.LoggerFactory;

public class LoginTests extends BaseTest {
	private static final Logger log = LoggerFactory.getLogger(LoginTests.class);

	@Test(description = "Valid login to OpenEMR")
	public void validLoginTest() {
		log.info("=== Starting test: validLoginTest ===");

		LoginPage loginPage = new LoginPage(driver);
		DashboardPage dashboardPage = loginPage.enterUsername("XET-admin-88").enterPassword("B@omar.1234567")
				.clickLogin();

		String title = dashboardPage.getPageTitle();
		log.info("Verifying dashboard title: {}", title);

		Assert.assertTrue(title.contains("OpenEMR"), "Dashboard title does not contain 'OpenEMR'");
		log.info("=== Completed test: validLoginTest ===");
	}

	@Test
	public void failTest() {
		Assert.fail("Intentional failure to test Allure screenshot");
	}
}

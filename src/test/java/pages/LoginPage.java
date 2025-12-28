package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

public class LoginPage extends BasePage {

	@FindBy(id = "authUser")
	private WebElement usernameInput;

	@FindBy(id = "clearPass")
	private WebElement passwordInput;

	@FindBy(xpath = "//button[@type='submit' or @id='login-button']")
	private WebElement loginButton;

	@FindBy(xpath = "//div[contains(@class,'alert-danger') or contains(text(),'Invalid')]")
	private WebElement errorMessage;

	public LoginPage(WebDriver driver) {
		super(driver);
		log.info("LoginPage initialized");
	}

	public LoginPage enterUsername(String username) {
		type(usernameInput, username, "Username Input");
		return this;
	}

	public LoginPage enterPassword(String password) {
		type(passwordInput, password, "Password Input");
		return this;
	}

	public DashboardPage clickLogin() {
		click(loginButton, "Login Button");
		log.info("Clicked on Login button");
		return new DashboardPage(driver);
	}

	public String getErrorMessage() {
		return getText(errorMessage, "Login Error Message");
	}
}

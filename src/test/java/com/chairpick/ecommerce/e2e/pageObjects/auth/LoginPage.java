package com.chairpick.ecommerce.e2e.pageObjects.auth;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import com.chairpick.ecommerce.e2e.pageObjects.customers.CreateCustomerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends PageObject {

    private final By emailInput = By.cssSelector("input#email");
    private final By passwordInput = By.cssSelector("input#password");
    private final By submitButton = By.cssSelector("button[type='submit']");
    private final By createAccountLink = By.cssSelector(".create_customer");
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public String fillEmail(String email) {
        driver.findElement(emailInput).sendKeys(email);
        return driver.findElement(emailInput).getDomProperty("value");
    }

    public String fillPassword(String password) {
        driver.findElement(passwordInput).sendKeys(password);
        return driver.findElement(passwordInput).getDomProperty("value");
    }

    public CreateCustomerPage goToCreateAccountPage() {
        driver.findElement(createAccountLink).click();
        return new CreateCustomerPage(driver);
    }

    public boolean isSubmitButtonEnabled() {
        String disabledAttribute = driver.findElement(submitButton).getAttribute("disabled");
        return disabledAttribute == null || !disabledAttribute.equals("true");
    }

    public void submit() {
        driver.findElement(submitButton).click();
    }
}

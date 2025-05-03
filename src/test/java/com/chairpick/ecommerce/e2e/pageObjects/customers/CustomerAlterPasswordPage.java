package com.chairpick.ecommerce.e2e.pageObjects.customers;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Objects;

public class CustomerAlterPasswordPage extends PageObject {
    private static String URL = "http://localhost:8080/customers/([1-9]+)/alter-password";
    private final By passwordInput = By.cssSelector("input#password");
    private final By passwordConfirmationInput = By.cssSelector("input#password_confirmation");
    private final By submitButton = By.cssSelector("button[type='submit']");

    public CustomerAlterPasswordPage(WebDriver driver) {
        super(driver);
    }

    public String fillPassword(String password) {
        driver.findElement(passwordInput).sendKeys(password);
        return driver.findElement(passwordInput).getDomProperty("value");
    }

    public String fillPasswordConfirmation(String password) {
        driver.findElement(passwordConfirmationInput).sendKeys(password);
        return driver.findElement(passwordConfirmationInput).getDomProperty("value");
    }

    public void submit() {
        driver.findElement(submitButton).click();
    }


    public void verifyIfIsTheCorrectPage() {
        if (!Objects.requireNonNull(driver.getCurrentUrl()).matches(URL)) {
            throw new IllegalStateException("This is not the correct page");
        }
    }
}

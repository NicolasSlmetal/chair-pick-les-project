package com.chairpick.ecommerce.e2e.pageObjects.customers;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Objects;

public class EditCustomerPage extends PageObject {
    private static final String URL = "http://localhost:8080/customers/edit";
    private final By nameInput = By.cssSelector("input#name");
    private final By cpfInput = By.cssSelector("input#cpf");
    private final By emailInput = By.cssSelector("input#email");
    private final By phoneInput = By.cssSelector("input#phone");
    private final By phoneTypeSelect = By.cssSelector("select#phone_type");
    private final By genreSelect = By.cssSelector("select#genre");
    private final By bornDateInput = By.cssSelector("input#birthdate");
    private final By submitButton = By.cssSelector("button[type='submit']");

    public EditCustomerPage(WebDriver driver) {
        super(driver);
    }

    public String changeName(String name) {
        driver.findElement(nameInput).clear();
        driver.findElement(nameInput).sendKeys(name);
        return driver.findElement(nameInput).getDomProperty("value");
    }

    public String getValueForName() {
        return driver.findElement(nameInput).getDomProperty("value");
    }

    public String getValueForCpf() {
        return driver.findElement(cpfInput).getDomProperty("value");
    }

    public String getValueForEmail() {
        return driver.findElement(emailInput).getDomProperty("value");
    }

    public String getValueForPhone() {
        return driver.findElement(phoneInput).getDomProperty("value");
    }

    public String getValueForPhoneType() {
        return driver.findElement(phoneTypeSelect).getDomProperty("value");
    }

    public String getValueForGenre() {
        return driver.findElement(genreSelect).getDomProperty("value");
    }

    public String getValueForBornDate() {
        return driver.findElement(bornDateInput).getDomProperty("value");
    }

    public void submit() {
        driver.findElement(submitButton).click();
    }

    @Override
    public void verifyIfIsTheCorrectPage() {
        if (!Objects.requireNonNull(driver.getCurrentUrl()).equals(URL)) {
            throw new IllegalStateException("This is not the edit customer page");
        }
    }
}

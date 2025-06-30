package com.chairpick.ecommerce.e2e.pageObjects.addresses;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EditAddressPage extends PageObject {

    private final By nameInput = By.cssSelector("input#name");
    private final By streetInput = By.cssSelector("input#address");
    private final By streetTypeInput = By.cssSelector("select#address_type");
    private final By cepInput = By.cssSelector("input#cep");
    private final By neighborhoodInput = By.cssSelector("input#neighborhood");
    private final By numberInput = By.cssSelector("input#number");
    private final By cityInput = By.cssSelector("input#city");
    private final By stateInput = By.cssSelector("input#state");
    private final By countryInput = By.cssSelector("input#country");
    private final By observationsInput = By.cssSelector("textarea#observations");
    private final By defaultAddressCheckbox = By.cssSelector("input#default_address");
    private final By submitButton = By.cssSelector("button[type='submit']");

    public EditAddressPage(WebDriver driver) {
        super(driver);
    }

    public String changeName(String name) {
        driver.findElement(nameInput).clear();
        driver.findElement(nameInput).sendKeys(name);
        return driver.findElement(nameInput).getDomProperty("value");
    }

    public boolean isSubmitButtonEnabled() {
        String disabledAttribute = driver.findElement(submitButton).getAttribute("disabled");
        return disabledAttribute == null || !disabledAttribute.equals("true");
    }

    public AddressHomePage submit() {
        driver.findElement(submitButton).click();
        return new AddressHomePage(driver);
    }
}

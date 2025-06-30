package com.chairpick.ecommerce.e2e.pageObjects.creditCards;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EditCreditCardPage extends PageObject {
    private By nameInput = By.cssSelector("input#card_name");
    private By numberInput = By.cssSelector("input#card_number");
    private By brandInput = By.cssSelector("select#card_brand");
    private By cvvInput = By.cssSelector("input#cvv");
    private By defaultCreditCardCheckbox = By.cssSelector("input#default_credit_card");
    private By submitButton = By.cssSelector("button[type='submit']");

    public EditCreditCardPage(WebDriver driver) {
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

    public CreditCardHomePage submit() {
        driver.findElement(submitButton).click();
        return new CreditCardHomePage(driver);
    }
}

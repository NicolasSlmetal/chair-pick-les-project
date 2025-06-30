package com.chairpick.ecommerce.e2e.pageObjects.creditCards;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class CreateCreditCardPage extends PageObject {
    private By nameInput = By.cssSelector("input#card_name");
    private By numberInput = By.cssSelector("input#card_number");
    private By brandInput = By.cssSelector("select#card_brand");
    private By cvvInput = By.cssSelector("input#cvv");
    private By defaultCreditCardCheckbox = By.cssSelector("input#default_credit_card");
    private By submitButton = By.cssSelector("button[type='submit']");
    public CreateCreditCardPage(WebDriver driver) {
        super(driver);
    }

    public String fillName(String name) {
        driver.findElement(nameInput).sendKeys(name);
        return driver.findElement(nameInput).getDomProperty("value");
    }

    public String fillNumber(String number) {
        driver.findElement(numberInput).sendKeys(number);
        return driver.findElement(numberInput).getDomProperty("value");
    }

    public String fillBrand(String brand) {
        WebElement element = driver.findElement(brandInput);
        Select select = new Select(element);
        select.selectByValue(brand);
        return element.getDomProperty("value");
    }

    public String fillCvv(String cvv) {
        driver.findElement(cvvInput).sendKeys(cvv);
        return driver.findElement(cvvInput).getDomProperty("value");
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

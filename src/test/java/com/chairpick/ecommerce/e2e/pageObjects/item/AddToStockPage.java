package com.chairpick.ecommerce.e2e.pageObjects.item;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import com.chairpick.ecommerce.e2e.pageObjects.chairs.AdminChairs;
import io.eotsevych.select2.Select2;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AddToStockPage extends PageObject {
    private final By amountInput = By.id("amount");
    private final By costInput = By.id("cost");
    private final By supplierInput = By.id("supplier");
    private final By entryDateInput = By.id("entry_date");
    private final By buttonSubmit = By.cssSelector("button[type='submit']");
    public AddToStockPage(WebDriver driver) {
        super(driver);
    }

    public String keyAmount(String amount) {
        WebElement element = driver.findElement(amountInput);
        element.sendKeys(amount);
        return element.getAttribute("value");
    }

    public String keyCost(String cost) {
        WebElement element = driver.findElement(costInput);
        element.sendKeys(cost);
        return element.getAttribute("value");
    }

    public String keyEntryDate(String entryDate) {
        WebElement element = driver.findElement(entryDateInput);
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        String script = "arguments[0].value = arguments[1]; " +
                "arguments[0].dispatchEvent(new Event('change'));" +
                "arguments[0].dispatchEvent(new Event('input'));";
        executor.executeScript(script, element, entryDate);
        return element.getAttribute("value");
    }

    public boolean isSubmitButtonEnabled() {
        WebElement submitButton = driver.findElement(buttonSubmit);
        String disabled = submitButton.getAttribute("disabled");
        return disabled == null || !disabled.equals("true");
    }

    public String keySupplier(String supplier) {
        WebElement element = driver.findElement(supplierInput);
        Select2 select2 = new Select2(element);
        select2.selectByText(supplier, false);
        ((JavascriptExecutor) driver).executeScript(
                "document.querySelector('#" + element.getAttribute("id") + "').dispatchEvent(new Event('change'))");
        return element.getAttribute("value");
    }

    public AdminChairs submit() {
        WebElement submitButton = driver.findElement(buttonSubmit);
        submitButton.click();
        return new AdminChairs(driver);
    }
}

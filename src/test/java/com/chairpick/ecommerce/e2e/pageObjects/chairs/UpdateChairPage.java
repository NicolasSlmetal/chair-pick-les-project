package com.chairpick.ecommerce.e2e.pageObjects.chairs;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class UpdateChairPage extends PageObject {
    private final By chairNameInput = By.id("name");
    private final By alterPriceSelect = By.id("alter_price");
    private final By alterPriceInput = By.id("price");
    private final By alterPriceReason = By.id("reason");
    private final By submitButton = By.cssSelector("button[type='submit']");

    public UpdateChairPage(WebDriver driver) {
        super(driver);
    }

    public String keyChairName(String chairName) {
        WebElement element = driver.findElement(chairNameInput);
        element.clear();
        element.sendKeys(chairName);
        return element.getAttribute("value");
    }

    public String keyAlterPrice(String alterPrice) {
        WebElement element = driver.findElement(alterPriceInput);
        element.clear();
        element.sendKeys(alterPrice);
        return element.getAttribute("value");
    }

    public String keyAlterPriceReason(String reason) {
        WebElement element = driver.findElement(alterPriceReason);
        element.clear();
        element.sendKeys(reason);
        return element.getAttribute("value");
    }

    public String selectToAlterPrice() {
        WebElement element = driver.findElement(alterPriceSelect);
        Select select = new Select(element);
        select.selectByVisibleText("Sim");
        notifyEvents(element);
        return element.getAttribute("value");
    }

    private void notifyEvents(WebElement element) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("arguments[0].dispatchEvent(new Event('change'));", element);
        jsExecutor.executeScript("arguments[0].dispatchEvent(new Event('input'));", element);
    }

    public String selectNotToAlterPrice() {
        WebElement element = driver.findElement(alterPriceSelect);
        Select select = new Select(element);
        select.selectByVisibleText("Não");
        notifyEvents(element);
        return element.getAttribute("value");
    }

    public boolean isSubmitButtonEnabled() {
        String disabled = driver.findElement(submitButton).getAttribute("disabled");
        return disabled == null || !disabled.equals("true");
    }

    public AdminChairs submit() {
        driver.findElement(submitButton).click();
        return new AdminChairs(driver);
    }
}

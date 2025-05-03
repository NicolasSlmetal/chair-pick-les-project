package com.chairpick.ecommerce.e2e.pageObjects.chairs;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import com.chairpick.ecommerce.e2e.pageObjects.cart.CartConfirmationPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ChairPage extends PageObject {

    private final By buyButton = By.id("buy");
    public ChairPage(WebDriver driver) {
        super(driver);
    }

    public CartConfirmationPage clickBuyButton(WebDriverWait wait) {
        wait.until(ExpectedConditions.presenceOfElementLocated(this.buyButton));
        WebElement buyButton = driver.findElement(this.buyButton);
        buyButton.click();
        return new CartConfirmationPage(driver);
    }

}

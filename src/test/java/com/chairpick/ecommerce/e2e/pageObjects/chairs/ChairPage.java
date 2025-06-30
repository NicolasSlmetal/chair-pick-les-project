package com.chairpick.ecommerce.e2e.pageObjects.chairs;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import com.chairpick.ecommerce.e2e.pageObjects.cart.CartConfirmationPage;
import com.chairpick.ecommerce.e2e.pageObjects.cart.CartPage;
import com.chairpick.ecommerce.e2e.pageObjects.index.IndexPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ChairPage extends IndexPage {

    private final By buyButton = By.id("buy");
    private final By cartButton = By.id("add_to_cart");
    public ChairPage(WebDriver driver) {
        super(driver);
    }

    public CartConfirmationPage clickBuyButton(WebDriverWait wait) {
        wait.until(ExpectedConditions.presenceOfElementLocated(this.buyButton));
        WebElement buyButton = driver.findElement(this.buyButton);
        buyButton.click();
        return new CartConfirmationPage(driver);
    }

    public CartPage addToCart(WebDriverWait wait) {
        wait.until(ExpectedConditions.presenceOfElementLocated(this.cartButton));
        WebElement buyButton = driver.findElement(this.cartButton);
        buyButton.click();
        return new CartPage(driver);
    }

}

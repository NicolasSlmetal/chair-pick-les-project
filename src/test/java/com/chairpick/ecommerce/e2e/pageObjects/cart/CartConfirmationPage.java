package com.chairpick.ecommerce.e2e.pageObjects.cart;

import com.chairpick.ecommerce.e2e.pageObjects.index.IndexPage;
import com.chairpick.ecommerce.e2e.pageObjects.orders.OrderPaymentPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class CartConfirmationPage extends IndexPage {
    private final By subtotalBuyText = By.id("subtotal");
    private final By freightText = By.id("freight_value");
    private final By totalText = By.id("total_value");
    private final By confirmButton = By.id("buy");
    public CartConfirmationPage(WebDriver driver) {
        super(driver);
    }

    public String getSubtotalBuyText() {
        WebElement subtotalH2 = driver.findElement(subtotalBuyText);
        moveToElement(subtotalH2);
        return subtotalH2.getText();
    }

    public String getFreightText() {
        WebElement freightH2 = driver.findElement(freightText);
        moveToElement(freightH2);
        return freightH2.getText();
    }

    public String getTotalText() {
        WebElement totalText = driver.findElement(this.totalText);
        moveToElement(totalText);
        return totalText.getText();
    }

    public OrderPaymentPage clickConfirmButton() {
        WebElement confirmButton = driver.findElement(this.confirmButton);
        moveToElement(confirmButton);
        confirmButton.click();
        return new OrderPaymentPage(driver);
    }

    private void moveToElement(WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).perform();
    }


}

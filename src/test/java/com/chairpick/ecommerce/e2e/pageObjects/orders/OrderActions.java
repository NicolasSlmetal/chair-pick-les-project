package com.chairpick.ecommerce.e2e.pageObjects.orders;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;
import java.util.function.Function;

public class OrderActions {

    private final WebDriver driver;
    Map<String, Function<WebDriver, PageObject>> pageMap = Map.of(
            "view_items", OrderItemsPage::new, "payment_status"
            , PaymentStatusPage::new);


    public OrderActions(WebDriver driver) {
        this.driver = driver;
    }

    public PageObject clickAction(String action) {
        WebElement menu = driver.findElement(By.cssSelector(".menu"));
        menu.findElement(By.cssSelector("." +action)).click();
        return pageMap.get(action).apply(driver);
    }
}

package com.chairpick.ecommerce.e2e.pageObjects.orders;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AdminOrderIndexPage extends PageObject  {

    private final By table = By.cssSelector("table");
    private final By logoutButton = By.cssSelector(".logout");
    public AdminOrderIndexPage(WebDriver driver) {
        super(driver);
    }

    public OrderActions getMenuDivOfRow(int row) {
        WebElement tableElement = driver.findElement(table);
        WebElement rowElement = tableElement.findElements(By.cssSelector("tr")).get(row);
        rowElement.findElement(By.cssSelector("tr:last-child img")).click();
        return new OrderActions(driver);
    }

    public void clickLogoutButton() {
        driver.findElement(logoutButton).click();
    }

}

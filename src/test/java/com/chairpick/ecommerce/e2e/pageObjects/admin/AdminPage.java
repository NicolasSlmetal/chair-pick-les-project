package com.chairpick.ecommerce.e2e.pageObjects.admin;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import com.chairpick.ecommerce.e2e.pageObjects.index.IndexPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AdminPage extends PageObject {
    protected final By adminStock = By.cssSelector(".stock");
    protected final By adminDashboard = By.cssSelector(".dashboard");
    protected final By adminCustomers = By.cssSelector(".customers");
    protected final By adminOrders = By.cssSelector(".orders");
    protected final By logoutButton = By.cssSelector(".logout");
    private final String actualPageClass;
    public AdminPage(WebDriver driver, String actualPageClass) {
        super(driver);
        this.actualPageClass = actualPageClass;
    }

    public IndexPage logout() {
        driver.findElement(logoutButton).click();
        return new IndexPage(driver);
    }


}

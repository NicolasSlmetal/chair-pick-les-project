package com.chairpick.ecommerce.e2e.pageObjects.orders;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import com.chairpick.ecommerce.e2e.pageObjects.components.ConfirmModal;
import com.chairpick.ecommerce.e2e.pageObjects.swap.SwapPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class OrderItemsPage extends PageObject {

    private final By table = By.cssSelector("table");
    private final By backButton = By.cssSelector("#back_button");
    private final By dispatchDeliveryButton = By.cssSelector(".dispatch");
    private final By confirmDeliveryButton = By.cssSelector(".delivered");
    private final By confirmActionModal = By.cssSelector("#confirm__modal");
    private final By redirectToSwaps = By.cssSelector(".action__button.swaps");

    public OrderItemsPage(WebDriver driver) {
        super(driver);
    }

    public ConfirmModal dispatchDeliveryOfRow(int row) {
        WebElement table = driver.findElement(this.table);
        WebElement rowElement = table.findElements(By.cssSelector("tr")).get(row);
        rowElement.findElement(dispatchDeliveryButton).click();
        return new ConfirmModal(driver.findElement(confirmActionModal));
    }

    public ConfirmModal confirmDeliveryOfRow(int row) {
        WebElement table = driver.findElement(this.table);
        WebElement rowElement = table.findElements(By.cssSelector("tr")).get(row);
        rowElement.findElement(confirmDeliveryButton).click();
        return new ConfirmModal(driver.findElement(confirmActionModal));
    }

    public String getStatusOfRow(int row) {
        WebElement table = driver.findElement(this.table);
        WebElement rowElement = table.findElements(By.cssSelector("tr")).get(row);
        return rowElement.findElement(By.cssSelector("td.status")).getText();
    }

    public void clickBackButton() {
        driver.findElement(backButton).click();
    }

    public SwapPage redirectToOrdersSwaps() {
        driver.findElement(redirectToSwaps).click();
        return new SwapPage(driver);
    }


}

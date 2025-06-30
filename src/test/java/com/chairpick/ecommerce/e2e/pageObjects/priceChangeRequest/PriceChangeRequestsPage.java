package com.chairpick.ecommerce.e2e.pageObjects.priceChangeRequest;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import com.chairpick.ecommerce.e2e.pageObjects.chairs.AdminChairs;
import com.chairpick.ecommerce.e2e.pageObjects.components.ConfirmModal;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;


public class PriceChangeRequestsPage extends PageObject {

    private final By rowSelector = By.cssSelector("tr");
    private final By columnSelector = By.cssSelector("td");
    private final By backButton = By.cssSelector(".back");
    public PriceChangeRequestsPage(WebDriver driver) {
        super(driver);
    }

    public AdminChairs goBackToAdminChairs() {
        driver.findElement(backButton).click();
        return new AdminChairs(driver);
    }

    public String getRequestedPriceOfRow(int rowIndex) {
        List<WebElement> rows = driver.findElements(rowSelector);
        if (!rows.isEmpty()) {
            List<WebElement> columns = rows.get(rowIndex).findElements(columnSelector);
            if (!columns.isEmpty()) {
                return columns.getFirst().getText();
            }
        }
        return null;
    }

    public String getReasonOfRow(int rowIndex) {
        List<WebElement> rows = driver.findElements(rowSelector);
        if (!rows.isEmpty()) {
            List<WebElement> columns = rows.get(rowIndex).findElements(columnSelector);
            if (columns.size() > 1) {
                return columns.get(1).getText();
            }
        }
        return null;
    }

    public String getStatusOfRow(int rowIndex) {
        List<WebElement> rows = driver.findElements(rowSelector);
        if (!rows.isEmpty()) {
            List<WebElement> columns = rows.get(rowIndex).findElements(columnSelector);
            if (columns.size() > 2) {
                return columns.get(2).getText();
            }
        }
        return null;
    }

    public List<WebElement> getActionsElements(int rowIndex) {
        List<WebElement> rows = driver.findElements(rowSelector);
        if (!rows.isEmpty()) {
            List<WebElement> columns = rows.get(rowIndex).findElements(columnSelector);
            if (columns.size() > 3) {
                return columns.get(3).findElements(By.cssSelector("button"));
            }
        }
        return List.of();
    }

    public ConfirmModal approvePriceChangeRequest(int rowIndex) {
        List<WebElement> actions = getActionsElements(rowIndex);
        if (!actions.isEmpty()) {
            actions.getFirst().click();
            WebElement modal = driver.findElement(By.id("confirm_action"));
            return new ConfirmModal(modal);
        }
        throw new IllegalStateException("No actions available for the specified row index");
    }

    public ConfirmModal rejectPriceChangeRequest(int rowIndex) {
        List<WebElement> actions = getActionsElements(rowIndex);
        if (actions.size() > 1) {
            actions.get(1).click();
            WebElement modal = driver.findElement(By.id("confirm_action"));
            return new ConfirmModal(modal);
        }
        throw new IllegalStateException("No actions available for the specified row index");
    }
 }

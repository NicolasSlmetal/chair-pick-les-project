package com.chairpick.ecommerce.e2e.pageObjects.customers;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CustomerHomePage extends PageObject {

    private final By anchorCreateCustomer = By.cssSelector("a[data-test='create-customer-button']");
    private final By tableCustomers = By.cssSelector("table");
    private final By searchMenuButton = By.cssSelector("img#search_options");
    private final By deleteConfirmDialog = By.cssSelector("dialog#remove__modal");
    private final By rowActionsButton = By.cssSelector("img.action__button");
    private final By editRowButton = By.cssSelector("a.action__button");
    private final By deleteRowButton = By.cssSelector("a.action__button.danger");
    private final By searchSubmitButton = By.cssSelector("button#search");

    public CustomerHomePage(WebDriver driver) {
        super(driver);
    }

    public void openSearchMenu() {
        driver.findElement(searchMenuButton).click();
    }

    public String searchForName(String name) {
        driver.findElement(By.cssSelector("input#name")).sendKeys(name);
        return driver.findElement(By.cssSelector("input#name")).getAttribute("value");
    }

    public void submitSearch() {
        driver.findElement(searchSubmitButton).click();
    }

    public Map<String, Object> getValuesForLastRow() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(tableCustomers));
        List<WebElement> rows = driver.findElements(By.tagName("tr"));
        if (rows.isEmpty() || rows.size() == 1) {
            return Map.of();
        }
        WebElement lastRow = rows.getLast();
        List<WebElement> columns = lastRow.findElements(By.tagName("td"));
        return Map.of(
                "rank", columns.get(0).getText(),
                "name", columns.get(1).getText(),
                "cpf", columns.get(2).getText(),
                "phone", columns.get(3).getText(),
                "genre", columns.get(4).getText(),
                "birthdate", columns.get(5).getText(),
                "email", columns.get(6).getText(),
                "active", columns.get(7).getText()
        );

    }
}

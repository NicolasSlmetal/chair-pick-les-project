package com.chairpick.ecommerce.e2e.pageObjects;

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

    private static final String URL = "http://localhost:8080/customers";
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
        verifyIfIsTheCorrectPage();
    }

    @Override
    public void verifyIfIsTheCorrectPage() {
        if (!Objects.equals(URL, driver.getCurrentUrl())) {
            throw new IllegalStateException("This is not the customer home page");
        }
    }

    public CreateCustomerPage accessCreateCustomerPage() {
        verifyIfIsTheCorrectPage();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement createCustomerButton = wait.until(
                ExpectedConditions.elementToBeClickable(anchorCreateCustomer)
        );

        createCustomerButton.click();
        wait.until(ExpectedConditions.urlToBe(URL + "/new"));
        return new CreateCustomerPage(driver);
    }

    public static String getUrl() {
        return URL;
    }

    public void openSearchMenu() {
        driver.findElement(searchMenuButton).click();
    }

    public String searchForName(String name) {
        driver.findElement(By.cssSelector("input#name")).sendKeys(name);
        return driver.findElement(By.cssSelector("input#name")).getAttribute("value");
    }

    public void submitSearch() {
        driver.findElement(By.cssSelector("button#search")).click();
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
                "name", columns.get(0).getText(),
                "cpf", columns.get(1).getText(),
                "phone", columns.get(2).getText(),
                "genre", columns.get(3).getText(),
                "birthdate", columns.get(4).getText(),
                "email", columns.get(5).getText(),
                "actions", columns.get(6).findElement(rowActionsButton),
                "edit", columns.get(7).findElement(editRowButton),
                "delete", columns.get(8).findElement(deleteRowButton)
        );

    }

    public EditCustomerPage editRow(WebElement rowEditButton) {
        rowEditButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(16));
        wait.until(ExpectedConditions.urlMatches(URL + "/edit/([1-9]+)"));
        return new EditCustomerPage(driver);
    }

    public void deleteRow(WebElement rowDeleteButton) {
        rowDeleteButton.click();
    }

    public String getTextFromDeleteConfirmDialog() {
        return driver.findElement(deleteConfirmDialog).findElement(By.tagName("p")).getText();
    }

    public void cancelDelete() {
        driver.findElement(deleteConfirmDialog).findElement(By.cssSelector("button#dialog__cancel__button")).click();
    }

    public void confirmDelete() {
        driver.findElement(deleteConfirmDialog).findElement(By.cssSelector("button#dialog__confirm__button")).click();
    }
}

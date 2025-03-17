package com.chairpick.ecommerce.e2e.pageObjects.addresses;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddressHomePage extends PageObject {
    private final static String URL = "http://localhost:8080/customers/([1-9]+)/addresses";
    private final By anchorCreateAddress = By.cssSelector("a[data-test='create-credit-card-button']");
    private final By tableAddress = By.cssSelector("table");
    private final By searchMenuButton = By.cssSelector("img#search_options");
    private final By deleteConfirmDialog = By.cssSelector("dialog#remove__modal");
    private final By editRowButton = By.cssSelector("a.action__button");
    private final By deleteRowButton = By.cssSelector("a.action__button.danger");

    public AddressHomePage(WebDriver driver) {
        super(driver);
    }

    public Map<String, Object> getValuesForLastRow() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(tableAddress));
        List<WebElement> rows = driver.findElements(By.tagName("tr"));
        if (rows.isEmpty() || rows.size() == 1) {
            return Map.of();
        }
        WebElement lastRow = rows.getLast();
        List<WebElement> columns = lastRow.findElements(By.tagName("td"));
        return constructAddressMap(columns);
    }

    private Map<String, Object> constructAddressMap(List<WebElement> columns) {
        Map<String, Object> address = new HashMap<>();
        address.put("name", columns.get(0).getText());
        address.put("street", columns.get(1).getText());
        address.put("streetType", columns.get(2).getText());
        address.put("cep", columns.get(3).getText());
        address.put("neighborhood", columns.get(4).getText());
        address.put("number", columns.get(5).getText());
        address.put("city", columns.get(6).getText());
        address.put("state", columns.get(7).getText());
        address.put("country", columns.get(8).getText());
        address.put("observations", columns.get(9).getText());
        address.put("edit", columns.get(10).findElement(editRowButton));
        address.put("delete", columns.get(11).findElement(deleteRowButton));
        return address;
    }

    @Override
    public void verifyIfIsTheCorrectPage() {
        if (!Objects.requireNonNull(driver.getCurrentUrl()).matches(URL)) {
            throw new IllegalStateException("This is not the correct page");
        }
    }
}

package com.chairpick.ecommerce.e2e.pageObjects.creditCards;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import com.chairpick.ecommerce.e2e.pageObjects.customers.CustomerHomePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreditCardHomePage extends PageObject {
    private final static String URL = "http://localhost:8080/customers/([1-9]+)/credit-cards";
    private final By anchorCreateCreditCard = By.cssSelector(".create__credit__card");
    private final By tableCreditCard = By.cssSelector("table");
    private final By searchMenuButton = By.cssSelector("img#search_options");
    private final By deleteConfirmDialog = By.cssSelector("dialog#remove__modal");
    private final By editRowButton = By.cssSelector("a.action__button");
    private final By deleteRowButton = By.cssSelector("a.action__button.danger");
    private final By backButton = By.cssSelector("a[data-test='back-button']");

    public CreditCardHomePage(WebDriver driver) {
        super(driver);
    }

    public CreateCreditCardPage goToCreateCreditCardPage() {
        driver.findElement(anchorCreateCreditCard).click();
        return new CreateCreditCardPage(driver);
    }

    public Map<String, Object> getValuesForLastRow() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(tableCreditCard));
        List<WebElement> rows = driver.findElements(By.tagName("tr"));
        if (rows.isEmpty() || rows.size() == 1) {
            return Map.of();
        }
        WebElement lastRow = rows.getLast();
        List<WebElement> columns = lastRow.findElements(By.tagName("td"));
        return Map.of(
                "number", columns.get(0).getText(),
                "name", columns.get(1).getText(),
                "brand", columns.get(2).getText(),
                "cvv", columns.get(3).getText(),
                "default", columns.get(4).getText(),
                "edit", columns.get(5).findElement(editRowButton)
        );
    }

    public void back() {
        driver.findElement(backButton).click();
    }

}

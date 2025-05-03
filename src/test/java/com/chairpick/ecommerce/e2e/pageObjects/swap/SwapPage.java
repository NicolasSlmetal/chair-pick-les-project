package com.chairpick.ecommerce.e2e.pageObjects.swap;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import com.chairpick.ecommerce.e2e.pageObjects.components.ConfirmModal;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SwapPage extends PageObject {

    public static class ConfirmSwapModal {
        private final By returnToStockCheckbox = By.cssSelector("input[type='checkbox']");
        private final By confirmButton = By.cssSelector("#confirm__swap__button");
        private final By cancelButton = By.cssSelector("#cancel__swap__button");
        private final WebElement modal;

        public ConfirmSwapModal(WebElement modal) {
            this.modal = modal;
        }

        public void clickReturnToStockCheckbox() {
            modal.findElement(returnToStockCheckbox).sendKeys(Keys.SPACE);
        }

        public void clickConfirmButton() {
            modal.findElement(confirmButton).click();
        }

        public void clickCancelButton() {
            modal.findElement(cancelButton).click();
        }

    }

    private final By backButton = By.cssSelector("#back_button");
    private final By table = By.cssSelector("table");
    private final By approveSwapButton = By.cssSelector(".accept");
    private final By rejectSwapButton = By.cssSelector(".reject");
    private final By changeToSwappedButton = By.cssSelector(".change_to_swapped");

    public SwapPage(WebDriver driver) {
        super(driver);
    }

    public ConfirmModal approveSwapOfRow(int row) {
        driver.findElement(table).findElements(By.cssSelector("tr")).get(row).findElement(approveSwapButton).click();
        return new ConfirmModal(driver.findElement(By.cssSelector("#confirm_action")));
    }

    public ConfirmModal reproveSwapOfRow(int row) {
        driver.findElement(table).findElements(By.cssSelector("tr")).get(row).findElement(rejectSwapButton).click();
        return new ConfirmModal(driver.findElement(By.cssSelector("#confirm_action")));
    }

    public String getStatusOfRow(int row) {
        WebElement table = driver.findElement(By.cssSelector("table"));
        WebElement rowElement = table.findElements(By.cssSelector("tr")).get(row);
        return rowElement.findElement(By.cssSelector("td.status")).getText();
    }

    public ConfirmSwapModal confirmSwapOfRow(int row) {
        driver.findElement(table).findElements(By.cssSelector("tr")).get(row).findElement(changeToSwappedButton).click();
        return new ConfirmSwapModal(driver.findElement(By.cssSelector("#confirm__swap__modal")));
    }

    public void clickBackButton() {
        driver.findElement(backButton).click();
    }


}

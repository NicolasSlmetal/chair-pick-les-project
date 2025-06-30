package com.chairpick.ecommerce.e2e.pageObjects.chairs;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import com.chairpick.ecommerce.e2e.pageObjects.admin.AdminPage;
import com.chairpick.ecommerce.e2e.pageObjects.item.AddToStockPage;
import com.chairpick.ecommerce.e2e.pageObjects.priceChangeRequest.PriceChangeRequestsPage;
import com.chairpick.ecommerce.model.enums.UserType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class AdminChairs extends AdminPage {

    public class ChairStatusChangeModal {
        private final By reasonInputSelector = By.id("reason");
        private final By cancelButtonSelector = By.id("cancel_button");
        private final By confirmButtonSelector = By.id("confirm_button");

        public String keyReason(String reason) {
            WebElement reasonInput = driver.findElement(reasonInputSelector);
            reasonInput.clear();
            reasonInput.sendKeys(reason);
            return reasonInput.getAttribute("value");
        }

        public void cancel() {
            WebElement cancelButton = driver.findElement(cancelButtonSelector);
            cancelButton.click();
        }

        public void confirm() {
            WebElement confirmButton = driver.findElement(confirmButtonSelector);
            confirmButton.click();
        }
    }

    public class Actions {
        private final List<WebElement> buttons;

        public Actions(List<WebElement> buttons) {
            this.buttons = buttons;
        }

        public AddToStockPage openAddStock() {
            if (buttons.size() > 2) {
                WebElement addStockButton = buttons.get(2);
                addStockButton.click();
                return new AddToStockPage(driver);
            }

            return null;
        }


        public PriceChangeRequestsPage openPriceChangeRequestAsSalesManager() {
            if (!buttons.isEmpty()) {
                WebElement priceChangeRequestsButton = buttons.getFirst();
                priceChangeRequestsButton.click();
                return new PriceChangeRequestsPage(driver);
            }
            return null;
        }

        public PriceChangeRequestsPage openPriceChangeRequests() {
            if (buttons.size() > 1) {
                WebElement priceChangeRequestsButton = buttons.get(1);
                priceChangeRequestsButton.click();
                return new PriceChangeRequestsPage(driver);
            }
            return null;
        }

        public UpdateChairPage openUpdateChair() {
            if (!buttons.isEmpty()) {
                WebElement updateButton = buttons.getFirst();
                updateButton.click();
                return new UpdateChairPage(driver);
            }
            return null;
        }

        public ChairStatusChangeModal openChangeStatus() {
            if (buttons.size() > 3) {
                WebElement changeStatusButton = buttons.get(3);
                changeStatusButton.click();
                return new ChairStatusChangeModal();
            }
            return null;
        }
    }

    private final By buttonCreateChairSelector = By.cssSelector(".create_chair");
    private final By tableSelector = By.cssSelector("table");
    private final By rowSelector = By.cssSelector("tr");
    private final By columnSelector = By.cssSelector("td");

    public AdminChairs(WebDriver driver) {
        super(driver, ".stock");
    }

    public CreateChairPage selectToCreateAChair() {
        WebElement createChairButton = driver.findElement(buttonCreateChairSelector);
        createChairButton.click();
        return new CreateChairPage(driver);
    }

    public String getChairNameFromRow(int rowIndex) {
        List<WebElement> rows = driver.findElements(rowSelector);

        WebElement row = rows.get(rowIndex);
        List<WebElement> columns = row.findElements(columnSelector);
        if (!columns.isEmpty()) {
            return columns.getFirst().getText();
        }
        return null;
    }

    public String getActualPrice(int rowIndex) {
        List<WebElement> rows = driver.findElements(rowSelector);

        WebElement row = rows.get(rowIndex);
        List<WebElement> columns = row.findElements(columnSelector);
        if (columns.size() > 1) {
            return columns.get(1).getText();
        }
        return null;
    }

    public String getDimensionsOfRow(int rowIndex) {
        List<WebElement> rows = driver.findElements(rowSelector);

        WebElement row = rows.get(rowIndex);
        List<WebElement> columns = row.findElements(columnSelector);
        if (columns.size() > 2) {
            return columns.get(2).getText();
        }
        return null;
    }

    public String getWeightOfRow(int rowIndex) {
        List<WebElement> rows = driver.findElements(rowSelector);

        WebElement row = rows.get(rowIndex);
        List<WebElement> columns = row.findElements(columnSelector);
        if (columns.size() > 3) {
            return columns.get(3).getText();
        }
        return null;
    }

    public String getPricingGroupOfRow(int rowIndex) {
        List<WebElement> rows = driver.findElements(rowSelector);

        WebElement row = rows.get(rowIndex);
        List<WebElement> columns = row.findElements(columnSelector);
        if (columns.size() > 4) {
            return columns.get(4).getText();
        }
        return null;
    }

    public String getStockOfRow(int rowIndex) {
        List<WebElement> rows = driver.findElements(rowSelector);

        WebElement row = rows.get(rowIndex);
        List<WebElement> columns = row.findElements(columnSelector);
        if (columns.size() > 5) {
            return columns.get(5).getText();
        }
        return null;
    }

    public String getHigherCostOfRow(int rowIndex) {
        List<WebElement> rows = driver.findElements(rowSelector);

        WebElement row = rows.get(rowIndex);
        List<WebElement> columns = row.findElements(columnSelector);
        if (columns.size() > 6) {
            return columns.get(6).getText();
        }
        return null;
    }

    public String getAverageRatingOfRow(int rowIndex) {
        List<WebElement> rows = driver.findElements(rowSelector);

        WebElement row = rows.get(rowIndex);
        List<WebElement> columns = row.findElements(columnSelector);
        if (columns.size() > 7) {
            return columns.get(7).getText();
        }
        return null;
    }


    public String getStatusOfRow(int rowIndex) {
        List<WebElement> rows = driver.findElements(rowSelector);

        WebElement row = rows.get(rowIndex);
        List<WebElement> columns = row.findElements(columnSelector);
        if (columns.size() > 8) {
            return columns.get(8).getText();
        }
        return null;
    }

    public String getLastEntryStockOfRow(int rowIndex) {
        List<WebElement> rows = driver.findElements(rowSelector);

        WebElement row = rows.get(rowIndex);
        List<WebElement> columns = row.findElements(columnSelector);
        if (columns.size() > 9) {
            return columns.get(9).getText();
        }
        return null;
    }

    public Actions openActionsOfRow(int rowIndex) {
        List<WebElement> rows = driver.findElements(rowSelector);

        WebElement row = rows.get(rowIndex);
        List<WebElement> columns = row.findElements(columnSelector);
        if (columns.size() > 10) {
            WebElement actionsColumn = columns.get(10);
            actionsColumn.click();
            List<WebElement> buttons = row.findElements(By.cssSelector(".menu a"));
            return new Actions(buttons);
        }
        return null;
    }



}

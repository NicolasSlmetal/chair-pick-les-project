package com.chairpick.ecommerce.e2e.pageObjects.customers;

import com.chairpick.ecommerce.e2e.pageObjects.addresses.AddressHomePage;
import com.chairpick.ecommerce.e2e.pageObjects.components.ConfirmModal;
import com.chairpick.ecommerce.e2e.pageObjects.creditCards.CreditCardHomePage;
import com.chairpick.ecommerce.e2e.pageObjects.index.IndexPage;
import com.chairpick.ecommerce.model.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;


public class ProfilePage extends IndexPage {

    private final By h2Selector = By.cssSelector(".profile__info h2");
    private final By editCustomerButton = By.cssSelector(".edit__customer");
    private final By editPasswordButton = By.cssSelector(".edit__password");
    private final By addressesButton = By.cssSelector(".addresses");
    private final By creditCardsButton = By.cssSelector(".credit__cards");
    private final By deleteAccountButton = By.id("delete_account");
    @Builder
    @Getter
    @ToString
    public static class Card {
        String productName;
        String productPrice;
        String productAmount;
        String productStatus;
        String productSubtotal;
        String productFreight;
        String productTotal;
        String swapAmount;
    }

    public static class SwapModal {
        private final WebElement modal;
        private final By swapAmount = By.cssSelector("#swap__amount");
        private final By swapButton = By.cssSelector("#confirm__button__swap");
        private final By cancelButton = By.cssSelector("#cancel__button__swap");


        public SwapModal(WebElement modal) {
            this.modal = modal;
        }

        public String getSwapAmount() {
            return modal.findElement(swapAmount).getDomProperty("value");
        }

        public void clickSwapButton() {
            modal.findElement(swapButton).click();
        }

        public void clickCancelButton() {
            modal.findElement(cancelButton).click();
        }



    }
    public enum OrdersStatus {
        PENDING("pending"),
        APPROVED("approved"),
        REPROVED("reproved");

        private final String status;

        OrdersStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status.toUpperCase();
        }
    }

    public ProfilePage(WebDriver driver) {
        super(driver);
    }

    public String getCustomerName() {
        List<WebElement> titleElements = driver.findElements(h2Selector);
        if (!titleElements.isEmpty()) {
            return titleElements.getFirst().getText().trim();
        }
        return null;
    }

    public String getCustomerEmail() {
        List<WebElement> titleElements = driver.findElements(h2Selector);
        if (titleElements.size() > 1) {
            return titleElements.get(1).getText().trim();
        }
        return null;
    }



    public String getCustomerCPF() {
        List<WebElement> titleElements = driver.findElements(h2Selector);
        if (titleElements.size() > 2) {
            return titleElements.get(2).getText().trim();
        }
        return null;
    }

    public String getCustomerPhone() {
        List<WebElement> titleElements = driver.findElements(h2Selector);
        if (titleElements.size() > 3) {
            return titleElements.get(3).getText().trim();
        }
        return null;
    }

    public String getCustomerCpf() {
        List<WebElement> titleElements = driver.findElements(h2Selector);
        if (titleElements.size() > 2) {
            return titleElements.get(2).getText().trim();
        }
        return null;
    }

    public String getCustomerPhoneType() {
        List<WebElement> titleElements = driver.findElements(h2Selector);
        if (titleElements.size() > 4) {
            return titleElements.get(4).getText().trim();
        }
        return null;
    }

    public EditCustomerPage accessEditCustomerPage() {
        WebElement editButton = driver.findElement(editCustomerButton);
        editButton.click();
        return new EditCustomerPage(driver);
    }

    public CustomerAlterPasswordPage accessEditPasswordPage() {
        WebElement editPasswordButtonElement = driver.findElement(editPasswordButton);
        editPasswordButtonElement.click();
        return new CustomerAlterPasswordPage(driver);
    }

    public AddressHomePage accessAddressesPage() {
        WebElement addressesButtonElement = driver.findElement(addressesButton);
        addressesButtonElement.click();
        return new AddressHomePage(driver);
    }

    public CreditCardHomePage accessCreditCardsPage() {
        WebElement creditCardsButtonElement = driver.findElement(creditCardsButton);
        creditCardsButtonElement.click();
        return new CreditCardHomePage(driver);
    }

    public ConfirmModal clickToDeleteAcount() {
        By confirmActionModal = By.cssSelector("#confirm__dialog");
        WebElement deleteButton = driver.findElement(deleteAccountButton);
        deleteButton.click();
        return new ConfirmModal(driver.findElement(confirmActionModal));
    }

    public ConfirmModal clickToCancelOrder(OrdersStatus status, int index) {
        String className = status.getStatus();
        By cancelButtonSelector = By.cssSelector("section.profile." + className + " .card:nth-child(" + index + ") .cancel_order");
        WebElement cancelButton = driver.findElement(cancelButtonSelector);
        cancelButton.click();
        By confirmActionModal = By.cssSelector("#confirm__dialog");
        return new ConfirmModal(driver.findElement(confirmActionModal));

    }

    public void scrollIntoSectionOfOrders(OrdersStatus status) {
        String className = status.getStatus();
        By sectionSelector = By.cssSelector("section.profile." + className);
        WebElement section = driver.findElement(sectionSelector);
        Actions actions = new Actions(driver);
        actions.moveToElement(section).perform();
    }

    public Card getCardFromSection(OrdersStatus status, int index, int itemIndex) {
        String className = status.getStatus();
        By cardSelector = By.cssSelector("section.profile." + className + " .card:nth-child(" + index + ")");
        WebElement card = driver.findElement(cardSelector);
        By itemDescriptionSelector = By.cssSelector(".card:nth-child(" + itemIndex + ") .item_description");
        WebElement itemDescription = card.findElement(itemDescriptionSelector);
        return createCard(itemDescription);

    }

    public Card getCardOfSection(OrdersStatus status, int index, int itemIndex) {
        String className = status.getStatus();
        By cardSelector = By.cssSelector("section.profile." + className + " .card");
        List<WebElement> cards = driver.findElements(cardSelector);
        if (cards.isEmpty()) {
            return null;
        }
        By itemDescriptionSelector = By.cssSelector(".item_description");
        List<WebElement> itemDescriptions = cards.get(index).findElements(itemDescriptionSelector);
        if (itemDescriptions.isEmpty() || itemIndex < 0 || itemIndex >= itemDescriptions.size()) {
            return null;
        }
        WebElement itemDescription = itemDescriptions.get(itemIndex);
        return createCard(itemDescription);
    }

    public String getAmountToBeSwapped(int index, int itemIndex) {
        By cardSelector = By.cssSelector("section.profile.APPROVED .card:nth-child(" + index + ")");
        WebElement card = driver.findElement(cardSelector);
        By itemDescriptionSelector = By.cssSelector(".card:nth-child(" + itemIndex + ") .item_description");
        WebElement itemDescription = card.findElement(itemDescriptionSelector);
        By swapAmountSelector = By.cssSelector(".swap_amount");
        return itemDescription.findElement(swapAmountSelector).getText().replace("Quantidade a ser trocada: ", "");
    }

    public SwapModal requestSwapOfCard(OrdersStatus status, int index) {
        String className = status.getStatus();
        By cardSelector = By.cssSelector("section.profile." + className + " .card:nth-child(" + index + ")");
        WebElement card = driver.findElement(cardSelector);
        By swapButtonSelector = By.cssSelector(".card:nth-child(" + index + ") .request_swap");
        WebElement swapButton = card.findElement(swapButtonSelector);
        swapButton.click();
        By swapModalSelector = By.cssSelector("#swap__confirmation");
        return new SwapModal(driver.findElement(swapModalSelector));
    }

    private static Card createCard(WebElement itemDescription) {
        String productName = itemDescription.findElement(By.cssSelector("h2.name")).getText().trim();
        String productPrice = itemDescription.findElement(By.cssSelector("h2.sell_price")).getText().replace("Preço de venda: ", "");
        String productAmount = itemDescription.findElement(By.cssSelector("h2.amount")).getText().replace("Quantidade: ", "");
        String productStatus = itemDescription.findElement(By.cssSelector("h2.item_status")).getText().replace("Status: ", "");
        String productSubtotal = itemDescription.findElement(By.cssSelector("h2.item_subtotal")).getText().replace("Subtotal: ", "");
        String productFreight = itemDescription.findElement(By.cssSelector("h2.item_freight")).getText().replace("Frete: ", "");
        String productTotal = itemDescription.findElement(By.cssSelector("h2.item_total")).getText().replace("Total: ", "");
        return Card.builder()
                .productName(productName)
                .productPrice(productPrice)
                .productAmount(productAmount)
                .productStatus(productStatus)
                .productPrice(productPrice)
                .productSubtotal(productSubtotal)
                .productFreight(productFreight)
                .productTotal(productTotal)
                .build();
    }

}

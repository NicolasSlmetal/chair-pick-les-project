package com.chairpick.ecommerce.e2e.pageObjects.cart;

import com.chairpick.ecommerce.e2e.pageObjects.index.IndexPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CartPage extends IndexPage {

    private final By productCardSelector = By.cssSelector(".product_card");
    private final By confirmCartButtonSelector = By.cssSelector(".product_action.expanded");
    private final By productInputQuantityAmount = By.cssSelector("input[name='quantity']");
    private final By increaseAmountButtonSelector = By.cssSelector(".quantity.up");
    private final By decreaseAmountButtonSelector = By.cssSelector(".quantity.down");
    private final By removeProductButtonSelector = By.cssSelector(".danger.expanded");
    private final By maxQuantitySelector = By.cssSelector("input[name='max_quantity']");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public List<WebElement> getAllProductsInCart() {
        return driver.findElements(productCardSelector);
    }

    public void increaseQuantityOfProductInCart(WebElement product) {
        WebElement increaseButton = product.findElement(increaseAmountButtonSelector);
        increaseButton.click();
    }

    public void increaseUntil(WebElement product, int maxQuantity) throws InterruptedException {
        int currentQuantity = getQuantityOfProductInCart(product);
        while (currentQuantity < maxQuantity) {
            increaseQuantityOfProductInCart(product);
            Thread.sleep(2000);
            currentQuantity = getQuantityOfProductInCart(product);
        }
    }

    public void decreaseQuantityOfProductInCart(WebElement product) {
        WebElement decreaseButton = product.findElement(decreaseAmountButtonSelector);
        decreaseButton.click();
    }

    public void removeProductButtonSelector(WebElement product) {
        WebElement removeButton = product.findElement(removeProductButtonSelector);
        removeButton.click();
    }

    public int getMaxQuantityOfProductInCart(WebElement product) {
        WebElement maxQuantityInput = product.findElement(maxQuantitySelector);
        String value = maxQuantityInput.getAttribute("value");
        if (value == null || value.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    public int getQuantityOfProductInCart(WebElement product) {
        WebElement quantityInput = product.findElement(productInputQuantityAmount);

        String value = quantityInput.getAttribute("value");
        if (value == null || value.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    public CartConfirmationPage confirmCart() {
        WebElement confirmButton = driver.findElement(confirmCartButtonSelector);
        confirmButton.click();
        return new CartConfirmationPage(driver);
    }

}

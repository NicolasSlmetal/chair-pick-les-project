package com.chairpick.ecommerce.e2e.pageObjects.index;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import com.chairpick.ecommerce.e2e.pageObjects.chairs.ChairPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Random;


public class IndexPage extends PageObject {

    public static enum HeaderOptions {
        LOGIN("login"),
        CART("cart"),
        PROFILE("profile"),
        LOGOUT("logout"),
        HOME("home");

        private final String option;

        HeaderOptions(String option) {
            this.option = option;
        }

        public String getOption() {
            return option;
        }
    }

    protected final By header = By.cssSelector("header");
    protected final By productsSelector = By.cssSelector(".product_card");

    public IndexPage(WebDriver driver) {
        super(driver);
    }

    public WebElement accessHeaderOption(HeaderOptions option) {
        WebElement headerElement = driver.findElement(header);
        String selector = String.format("[data-test='%s']", option.getOption());
        WebElement optionElement = headerElement.findElement(By.cssSelector(selector));
        optionElement.click();
        return optionElement;
    }

    public ChairPage selectAnyProduct() {
        List<WebElement> products = driver.findElements(productsSelector);

        if (products.isEmpty()) {
            throw new IllegalStateException("No products found on the page");
        }

        Random random = new Random();
        WebElement product = products.get(random.nextInt(0, products.size()));
        product.click();
        return new ChairPage(driver);
    }


}

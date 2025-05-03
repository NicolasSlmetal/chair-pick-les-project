package com.chairpick.ecommerce.e2e.pageObjects.orders;

import com.chairpick.ecommerce.e2e.pageObjects.customers.ProfilePage;
import com.chairpick.ecommerce.e2e.pageObjects.index.IndexPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class OrderPaymentPage extends IndexPage {

    private final By creditCardDivsSelector = By.cssSelector("#credit_cards .card");
    private final By confirmButton = By.cssSelector("button[type='submit']");

    public OrderPaymentPage(WebDriver driver) {
        super(driver);
    }

    public Double getValueInActiveCreditCards() {
        List<WebElement> creditCardDivs = driver.findElements(creditCardDivsSelector);
        return creditCardDivs.stream()
                .filter(card -> card.findElement(By.cssSelector("input[type='checkbox']")).getDomProperty("checked").equals("true"))
                .map(card -> {
                    String beforeText = card.findElement(By.cssSelector(".payment_value")).getAttribute("value");

                    String text = beforeText
                            .replace(",", ".").replaceAll("[^0-9.]", "");
                    return Double.parseDouble(text);
                })
                .reduce(0.0, Double::sum);
    }

    public ProfilePage confirmOrder() {
        driver.findElement(confirmButton).click();
        return new ProfilePage(driver);
    }

}

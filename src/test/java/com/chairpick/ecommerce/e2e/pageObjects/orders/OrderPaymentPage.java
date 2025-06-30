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

    public List<WebElement> getAllPromotionalCoupons() {
        return driver.findElements(By.cssSelector("#promo_coupons .card"));
    }

    public double getPromotionalCouponsValues() {
        return getAllPromotionalCoupons()
                .stream().map(
                        coupon -> coupon.findElement(By.cssSelector("input[name='couponValue']"))
                ).map(input -> Double.parseDouble(input.getAttribute("value")))
                .reduce(0.0, Double::sum);
    }

    public double getSwapCouponsValues() {
        return getAllSwapCoupons()
                .stream().map(
                        coupon -> coupon.findElement(By.cssSelector("input[name='couponValue']"))
                ).map(input -> Double.parseDouble(input.getAttribute("value")))
                .reduce(0.0, Double::sum);
    }

    public void selectPromotionalCouponWithId(Long id) {
        List<WebElement> promoCoupons = getAllPromotionalCoupons();

        promoCoupons.stream()
                .filter(c -> getIdOfPayableComponent(c).equals(String.valueOf(id)))
                .findFirst().ifPresent(WebElement::click);
    }

    public void selectSwapCouponWithId(Long id) {
        List<WebElement> swapCoupons = getAllSwapCoupons();
        swapCoupons.stream()
                .filter(c -> getIdOfPayableComponent(c).equals(String.valueOf(id)))
                .findFirst().ifPresent(WebElement::click);
    }

    private static String getIdOfPayableComponent(WebElement c) {
        return c.findElement(By.cssSelector("input[type='hidden']")).getDomAttribute("value");
    }

    public String getExcessCouponValueMessage() {
        WebElement excessCouponMessage = driver.findElement(By.cssSelector("#generate_swap_coupon_message"));
        return excessCouponMessage.getText();
    }

    public List<WebElement> getAllSwapCoupons() {
        return driver.findElements(By.cssSelector("#swap_coupons .card"));
    }

    public ProfilePage confirmOrder() {
        driver.findElement(confirmButton).click();
        return new ProfilePage(driver);
    }

}

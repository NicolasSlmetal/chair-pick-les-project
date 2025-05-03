package com.chairpick.ecommerce.e2e.pageObjects.orders;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import com.chairpick.ecommerce.e2e.pageObjects.components.ConfirmModal;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PaymentStatusPage extends PageObject {

    private final By backButton = By.cssSelector("#back_button");
    private final By approveButton = By.cssSelector("#approve");
    private final By rejectButton = By.cssSelector("#reject");
    private final By confirmActionModal = By.cssSelector("#confirm__dialog");

    public PaymentStatusPage(WebDriver driver) {
        super(driver);
    }


    public ConfirmModal clickApprove() {
        driver.findElement(approveButton).click();
        return new ConfirmModal(driver.findElement(confirmActionModal));
    }

    public ConfirmModal clickReject() {
        driver.findElement(rejectButton).click();
        return new ConfirmModal(driver.findElement(confirmActionModal));
    }

    public boolean isApproveButtonEnabled() {
        try {
            WebElement approveButton = driver.findElement(this.approveButton);
            return approveButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRejectButtonEnabled() {
        try {
            WebElement rejectButton = driver.findElement(this.rejectButton);
            return rejectButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickBackButton() {
        driver.findElement(backButton).click();
    }
}

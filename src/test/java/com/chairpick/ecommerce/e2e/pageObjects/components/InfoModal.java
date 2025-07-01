package com.chairpick.ecommerce.e2e.pageObjects.components;

import com.chairpick.ecommerce.e2e.pageObjects.customers.ProfilePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class InfoModal {

    private final WebElement modal;
    private final By modalTitle = By.cssSelector("h1");
    private final By modalMessage = By.cssSelector("p");
    private final By modalOkButton = By.cssSelector("button");

    public InfoModal(WebElement modal) {
        this.modal = modal;
    }

    public String getTitle() {
        return modal.findElement(modalTitle).getText();
    }

    public String getMessage() {
        return modal.findElement(modalMessage).getText();
    }

    public void clickOkButton() {
        modal.findElement(modalOkButton).click();
    }


}

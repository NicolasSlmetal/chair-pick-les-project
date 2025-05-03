package com.chairpick.ecommerce.e2e.pageObjects.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ConfirmModal {

    private final WebElement modal;

    public ConfirmModal(WebElement modal) {
        this.modal = modal;
    }

    private final By confirmActionModal = By.cssSelector("#confirm__button");
    private final By cancelActionModal = By.cssSelector("#cancel__button");

    public void confirm() {
        modal.findElement(confirmActionModal).click();
    }

    public void cancel() {
        modal.findElement(cancelActionModal).click();
    }
}

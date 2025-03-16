package com.chairpick.ecommerce.e2e.pageObjects;

import org.openqa.selenium.WebDriver;

public abstract class PageObject {

    protected WebDriver driver;

    public PageObject(WebDriver driver) {
        this.driver = driver;
    }

    public abstract void verifyIfIsTheCorrectPage();
}

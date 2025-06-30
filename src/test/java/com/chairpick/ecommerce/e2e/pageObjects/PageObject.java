package com.chairpick.ecommerce.e2e.pageObjects;

import lombok.Getter;
import org.openqa.selenium.WebDriver;

@Getter
public abstract class PageObject {

    protected WebDriver driver;

    public PageObject(WebDriver driver) {
        this.driver = driver;
    }

    
}

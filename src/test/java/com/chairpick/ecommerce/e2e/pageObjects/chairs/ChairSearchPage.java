package com.chairpick.ecommerce.e2e.pageObjects.chairs;


import com.chairpick.ecommerce.e2e.pageObjects.index.IndexPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ChairSearchPage extends IndexPage {
    private final By searchedChairsSelector = By.cssSelector(".product_card");

    public ChairSearchPage(WebDriver driver) {
        super(driver);
    }


    public List<WebElement> getAllSearchedChairs() {
        return driver.findElements(searchedChairsSelector);
    }
}

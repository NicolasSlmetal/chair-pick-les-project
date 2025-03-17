package com.chairpick.ecommerce.e2e.pageObjects.customers;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import com.chairpick.ecommerce.e2e.pageObjects.addresses.AddressHomePage;
import com.chairpick.ecommerce.e2e.pageObjects.creditCards.CreditCardHomePage;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ActionsPageMap {

    private static ActionsPageMap instance;
    private final Map<String, Function<WebDriver, PageObject>> pageMap;

    private ActionsPageMap() {
        pageMap = new HashMap<>();
        pageMap.put("alterPassword", CustomerAlterPasswordPage::new);
        pageMap.put("creditCards", CreditCardHomePage::new);
        pageMap.put("addresses", AddressHomePage::new);
    }

    public static ActionsPageMap getInstance() {
        if (instance == null) {
            instance = new ActionsPageMap();
        }
        return instance;
    }



    public PageObject getPage(String pageName, WebDriver driver) {
        return pageMap.get(pageName).apply(driver);
    }
}

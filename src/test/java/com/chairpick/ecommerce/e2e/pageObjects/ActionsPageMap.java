package com.chairpick.ecommerce.e2e.pageObjects;

import com.chairpick.ecommerce.e2e.factories.WebDriverFactory;
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

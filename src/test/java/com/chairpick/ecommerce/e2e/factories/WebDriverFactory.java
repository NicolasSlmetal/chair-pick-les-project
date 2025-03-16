package com.chairpick.ecommerce.e2e.factories;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebDriverFactory {

    public static WebDriver createWebDriver() {
        return new ChromeDriver();
    }
}

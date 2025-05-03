package com.chairpick.ecommerce.e2e.factories;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebDriverFactory {

    public static WebDriver createWebDriver() {

        ChromeDriver driver = new ChromeDriver();
        driver.manage().window().fullscreen();
        return driver;
    }
}

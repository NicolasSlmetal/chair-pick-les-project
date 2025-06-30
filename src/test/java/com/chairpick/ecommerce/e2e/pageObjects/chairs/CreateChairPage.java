package com.chairpick.ecommerce.e2e.pageObjects.chairs;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;

public class CreateChairPage extends PageObject {
    private final By chairNameInput = By.id("name");
    private final By chairDescriptionInput = By.id("description");
    private final By chairCategoryInput = By.id("categories");
    private final By chairLengthInput = By.id("length");
    private final By chairWidthInput = By.id("width");
    private final By chairHeightInput = By.id("height");
    private final By chairPricingGroupInput = By.id("pricing_group");
    private final By chairRating = By.id("rating");
    private final By chairImageInput = By.id("image");
    private final By buttonSubmit = By.cssSelector("button[type='submit']");



    public CreateChairPage(WebDriver driver) {
        super(driver);
    }

    public String keyName(String name) {
        WebElement element = driver.findElement(chairNameInput);
        element.sendKeys(name);
        return element.getAttribute("value") ;
    }

    public String keyDescription(String description) {
        WebElement element = driver.findElement(chairDescriptionInput);
        element.sendKeys(description);
        return element.getAttribute("value");
    }

    public String keyCategory(String category) {
        WebElement catInput = driver.findElement(chairCategoryInput);
        WebElement select2InputNearest = driver.findElements(By.cssSelector(".select2-search__field"))
                .stream().min(Comparator.comparing(element -> calculateDistance(element, catInput))).orElse(null);
        if (select2InputNearest == null) {
            return "";
        }
        select2InputNearest.sendKeys(category);
        select2InputNearest.sendKeys(Keys.ENTER);
        return catInput.getAttribute("value");

    }

    public String keyLength(String length) {
        WebElement element = driver.findElement(chairLengthInput);
        element.sendKeys(length);
        return element.getAttribute("value");
    }

    public String keyWidth(String width) {
        WebElement element = driver.findElement(chairWidthInput);
        element.sendKeys(width);
        return element.getAttribute("value");
    }

    public String keyHeight(String height) {
        WebElement element = driver.findElement(chairHeightInput);
        element.sendKeys(height);
        return element.getAttribute("value");
    }

    public String keyWeight(String weight) {
        WebElement element = driver.findElement(By.id("weight"));
        element.sendKeys(weight);
        return element.getAttribute("value");
    }

    public String keyPricingGroup(String pricingGroupId) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        String script = """
            var $ = window.jQuery;
            if (!$) return 'jQuery not found';
        
            var select = $('#pricing_group');
            
            var valToSelect = arguments[0];
            var textToSelect = arguments[1];
            
            if (!select.length) return 'select not found';
        
            select.val(valToSelect).trigger('change');
        
            // Opcional: dispara evento select para forçar update visual
            if (select.hasClass('select2-hidden-accessible')) {
                select.select2('trigger', 'select', {
                    data: { id: valToSelect, text: textToSelect }
                });
            }
        
            return valToSelect;
        """;

        Object returnValue = js.executeScript(script, pricingGroupId);
        if (returnValue == null) {
            return "";
        }
        return returnValue.toString();
    }


    public String keyRating(String rating) {
        WebElement element = driver.findElement(chairRating);
        element.sendKeys(rating);
        return element.getAttribute("value");
    }

    public String keyImage(Path path) {
        WebElement element = driver.findElement(chairImageInput);
        element.sendKeys(path.toAbsolutePath().toString());
        return element.getAttribute("value");
    }

    public boolean isSubmitButtonEnabled() {
        WebElement submitButton = driver.findElement(buttonSubmit);
        String disabled = submitButton.getAttribute("disabled");
        return disabled == null || disabled.equals("true");
    }

    public AdminChairs submitChair() {
        WebElement submitButton = driver.findElement(buttonSubmit);
        submitButton.click();
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.urlContains("/admin/chairs"));
        return new AdminChairs(driver);
    }

    private double calculateDistance(WebElement el1, WebElement el2) {
        int x1 = el1.getLocation().getX();
        int y1 = el1.getLocation().getY();
        int x2 = el2.getLocation().getX();
        int y2 = el2.getLocation().getY();
        double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        System.out.println("Distance between elements: " + distance);
        System.out.println("Ids of elements: " + el1.getAttribute("id") + " and " + el2.getAttribute("id"));
        return distance;
    }

}

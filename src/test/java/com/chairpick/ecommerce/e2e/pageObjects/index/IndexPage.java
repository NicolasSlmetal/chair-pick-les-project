package com.chairpick.ecommerce.e2e.pageObjects.index;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import com.chairpick.ecommerce.e2e.pageObjects.chairs.ChairPage;
import com.chairpick.ecommerce.e2e.pageObjects.chairs.ChairSearchPage;
import com.chairpick.ecommerce.e2e.pageObjects.chatbot.ChatBotPage;
import org.openqa.selenium.*;

import java.util.List;
import java.util.Random;


public class IndexPage extends PageObject {

    public static enum HeaderOptions {
        LOGIN("login"),
        CART("cart"),
        PROFILE("profile"),
        LOGOUT("logout"),
        HOME("home");

        private final String option;

        HeaderOptions(String option) {
            this.option = option;
        }

        public String getOption() {
            return option;
        }
    }

    protected final By header = By.cssSelector("header");
    protected final By productsSelector = By.cssSelector(".product_card");
    protected final By searchInputSelector = By.cssSelector("#name_search");
    protected final By searchButtonSelector = By.cssSelector(".home_search");
    private final By categorySearchInputSelector = By.cssSelector("#category");
    private final By minPriceSearchInputSelector = By.cssSelector("#min_price");
    private final By maxPriceSearchInputSelector = By.cssSelector("#max_price");
    private final By minLengthSearchInputSelector = By.cssSelector("#min_length");
    private final By maxLengthSearchInputSelector = By.cssSelector("#max_length");
    private final By minWidthSearchInputSelector = By.cssSelector("#min_width");
    private final By maxWidthSearchInputSelector = By.cssSelector("#max_width");
    private final By minHeightSearchInputSelector = By.cssSelector("#min_height");
    private final By maxHeightSearchInputSelector = By.cssSelector("#max_height");
    private final By minWeightSearchInputSelector = By.cssSelector("#min_weight");
    private final By maxWeightSearchInputSelector = By.cssSelector("#max_weight");
    private final By minAverageRatingInputSelector = By.cssSelector("#min_rating");
    private final By maxAverageRatingInputSelector = By.cssSelector("#max_rating");
    private final By chatBotButtonSelector = By.cssSelector(".bot_button");

    public IndexPage(WebDriver driver) {
        super(driver);
    }

    public WebElement accessHeaderOption(HeaderOptions option) {
        WebElement headerElement = driver.findElement(header);
        String selector = String.format("[data-test='%s']", option.getOption());
        WebElement optionElement = headerElement.findElement(By.cssSelector(selector));
        optionElement.click();
        return optionElement;
    }

    public ChatBotPage clickChatBotButton() {
        WebElement chatBotButton = driver.findElement(chatBotButtonSelector);
        chatBotButton.click();
        return new ChatBotPage(driver);
    }

    public ChairPage selectAnyProduct() {
        List<WebElement> products = driver.findElements(productsSelector);

        if (products.isEmpty()) {
            throw new IllegalStateException("No products found on the page");
        }

        Random random = new Random();
        WebElement product = products.get(random.nextInt(0, products.size()));
        product.click();
        return new ChairPage(driver);
    }

    public List<WebElement> getAllProducts() {
        return driver.findElements(productsSelector);
    }

    public ChairPage selectProductOfPosition(int position) {
        List<WebElement> products = driver.findElements(productsSelector);

        if (position < 0 || position >= products.size()) {
            throw new IndexOutOfBoundsException("Position is out of bounds");
        }

        WebElement product = products.get(position);
        product.click();
        return new ChairPage(driver);
    }

    public String writeTextToSearch(String text) {
        WebElement searchInput = driver.findElement(searchInputSelector);
        searchInput.clear();
        searchInput.sendKeys(text);
        return searchInput.getAttribute("value");
    }

    public String writeCategoryToSearch(String category) {
        WebElement categoryInput = driver.findElement(categorySearchInputSelector);
        WebElement select2Input = driver.findElement(By.cssSelector("input.select2-search__field"));
        select2Input.sendKeys(category);
        select2Input.sendKeys(Keys.ENTER);

        return categoryInput.getAttribute("value");
    }

    public String writeMinPriceToSearch(String minPrice) {
        WebElement minPriceInput = driver.findElement(minPriceSearchInputSelector);
        minPriceInput.clear();
        minPriceInput.sendKeys(minPrice);
        return minPriceInput.getAttribute("value");
    }

    public String writeMaxPriceToSearch(String maxPrice) {
        WebElement maxPriceInput = driver.findElement(maxPriceSearchInputSelector);
        maxPriceInput.clear();
        maxPriceInput.sendKeys(maxPrice);
        return maxPriceInput.getAttribute("value");
    }

    public String writeMinLengthToSearch(String minLength) {
        WebElement minLengthInput = driver.findElement(minLengthSearchInputSelector);
        minLengthInput.clear();
        minLengthInput.sendKeys(minLength);
        return minLengthInput.getAttribute("value");
    }

    public String writeMaxLengthToSearch(String maxLength) {
        WebElement maxLengthInput = driver.findElement(maxLengthSearchInputSelector);
        maxLengthInput.clear();
        maxLengthInput.sendKeys(maxLength);
        return maxLengthInput.getAttribute("value");
    }

    public String writeMinWidthToSearch(String minWidth) {
        WebElement minWidthInput = driver.findElement(minWidthSearchInputSelector);
        minWidthInput.clear();
        minWidthInput.sendKeys(minWidth);
        return minWidthInput.getAttribute("value");
    }

    public String writeMaxWidthToSearch(String maxWidth) {
        WebElement maxWidthInput = driver.findElement(maxWidthSearchInputSelector);
        maxWidthInput.clear();
        maxWidthInput.sendKeys(maxWidth);
        return maxWidthInput.getAttribute("value");
    }

    public String writeMinHeightToSearch(String minHeight) {
        WebElement minHeightInput = driver.findElement(minHeightSearchInputSelector);
        minHeightInput.clear();
        minHeightInput.sendKeys(minHeight);
        return minHeightInput.getAttribute("value");
    }

    public String writeMaxHeightToSearch(String maxHeight) {
        WebElement maxHeightInput = driver.findElement(maxHeightSearchInputSelector);
        maxHeightInput.clear();
        maxHeightInput.sendKeys(maxHeight);
        return maxHeightInput.getAttribute("value");
    }

    public String writeMinWeightToSearch(String minWeight) {
        WebElement minWeightInput = driver.findElement(minWeightSearchInputSelector);
        minWeightInput.clear();
        minWeightInput.sendKeys(minWeight);
        return minWeightInput.getAttribute("value");
    }

    public String writeMaxWeightToSearch(String maxWeight) {
        WebElement maxWeightInput = driver.findElement(maxWeightSearchInputSelector);
        maxWeightInput.clear();
        maxWeightInput.sendKeys(maxWeight);
        return maxWeightInput.getAttribute("value");
    }

    public String writeMinAverageRatingToSearch(String minAverageRating) {
        WebElement minAverageRatingInput = driver.findElement(minAverageRatingInputSelector);
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].value = '%s';arguments[0].dispatchEvent(new Event('input'))".formatted(minAverageRating), minAverageRatingInput);
        return minAverageRatingInput.getAttribute("value");
    }

    public String writeMaxAverageRatingToSearch(String maxAverageRating) {
        WebElement maxAverageRatingInput = driver.findElement(maxAverageRatingInputSelector);
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].value = '%s'; arguments[0].dispatchEvent(new Event('input'))".formatted(maxAverageRating), maxAverageRatingInput);
        return maxAverageRatingInput.getAttribute("value");
    }


    public ChairSearchPage clickSearchButton() {
        WebElement searchButton = driver.findElement(searchButtonSelector);
        searchButton.click();
        return new ChairSearchPage(driver);
    }


}

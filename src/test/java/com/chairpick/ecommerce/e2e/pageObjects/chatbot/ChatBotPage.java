package com.chairpick.ecommerce.e2e.pageObjects.chatbot;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;

import java.time.Duration;

public class ChatBotPage extends PageObject {
    private final By promptInput = By.id("user__input");
    private final By sendButton = By.id("send__button");
    private final By userMessageSelector = By.cssSelector(".user__message");
    private final By botMessageSelector = By.cssSelector(".bot__message");

    public ChatBotPage(WebDriver driver) {
        super(driver);
    }

    public String keyPrompt(String prompt) {
        WebElement inputField = driver.findElement(promptInput);
        inputField.sendKeys(prompt);
        return inputField.getAttribute("value");
    }

    public void clickSendButton() {
        WebElement button = driver.findElement(sendButton);
        button.click();
    }

    public String getLastUserMessage() {
        WebElement lastUserMessage = driver.findElements(userMessageSelector).stream()
                .reduce((first, second) -> second)
                .orElse(null);
        if (lastUserMessage == null) {
            return "";
        }

        WebElement p = lastUserMessage.findElement(By.tagName("p"));
        return p.getText();
    }

    public List<WebElement> getLastBotMessage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        wait.until(driver -> {
            List<WebElement> allMessages = driver.findElements(botMessageSelector);
            if (allMessages.isEmpty()) return false;

            WebElement lastMessage = allMessages.getLast();
            List<WebElement> cards = lastMessage.findElements(By.cssSelector(".product__card"));
            List<WebElement> paragraphs = lastMessage.findElements(By.tagName("p"));

            return !cards.isEmpty();
        });

        WebElement lastBotMessage = driver.findElements(botMessageSelector).stream()
                .reduce((first, second) -> second)
                .orElseThrow();

        return lastBotMessage.findElements(By.cssSelector(".product__card"));
    }

    public String getAdviceOnLastBotMessage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        wait.until(driver -> {
            List<WebElement> allMessages = driver.findElements(botMessageSelector);
            if (allMessages.isEmpty()) return false;

            WebElement lastMessage = allMessages.getLast();
            List<WebElement> cards = lastMessage.findElements(By.cssSelector(".product__card"));
            List<WebElement> paragraphs = lastMessage.findElements(By.tagName("p"));

            return !cards.isEmpty() ||
                   paragraphs.stream().anyMatch(p -> !p.getText().isEmpty());
        });

        WebElement lastBotMessage = driver.findElements(botMessageSelector).stream()
                .reduce((first, second) -> second)
                .orElseThrow();

        return lastBotMessage.getText().trim();
    }

    public List<WebElement> getLastBotMessageAfter(int initialMessageCount) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        wait.until(driver -> {
            List<WebElement> messages = driver.findElements(botMessageSelector);

            return messages.size() > initialMessageCount;
        });

        WebElement lastMessage = driver.findElements(botMessageSelector).stream()
                .reduce((first, second) -> second)
                .orElseThrow();

        wait.until(driver -> !lastMessage.findElements(By.cssSelector(".product__card")).isEmpty() ||
                lastMessage.findElements(By.tagName("p")).stream().anyMatch(p -> !p.getText().isEmpty()));

        return lastMessage.findElements(By.cssSelector(".product__card"));
    }

}

package com.chairpick.ecommerce.e2e.pages.chatbot;

import com.chairpick.ecommerce.e2e.factories.WebDriverFactory;
import com.chairpick.ecommerce.e2e.pageObjects.chairs.ChairPage;
import com.chairpick.ecommerce.e2e.pageObjects.chatbot.ChatBotPage;
import com.chairpick.ecommerce.e2e.pageObjects.index.IndexPage;
import com.chairpick.ecommerce.e2e.utils.ChairInitializer;
import com.chairpick.ecommerce.e2e.utils.ContainerInitializer;
import com.chairpick.ecommerce.e2e.utils.DatabaseSeeder;
import com.chairpick.ecommerce.e2e.utils.QdrantInitializer;
import io.qdrant.client.QdrantClient;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test-ai")
public class ChatBotTest {

    @Autowired
    private DatabaseSeeder seeder;
    @Autowired
    private QdrantClient qdrantClient;
    @Autowired
    private EmbeddingModel embeddingModel;
    private WebDriver driver;
    private final String BASE_URL = "http://localhost:8080/";
    private WebDriverWait wait;

    @BeforeAll
    public static void beforeAll() {
        try {
            ContainerInitializer.up();
            Thread.sleep(1000);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize containers", e);
        }
    }

    @BeforeEach
    public void setUp() {

        driver = WebDriverFactory.createWebDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.manage().window().maximize();

        QdrantInitializer qdrantInitializer = new QdrantInitializer(qdrantClient, seeder, embeddingModel);
        try {
            qdrantInitializer.seedDefaultProducts();
        } catch (Exception e) {
            throw new RuntimeException("Failed to seed default products", e);
        }

    }

    @Test
    public void shouldSuggestTheBestChair() throws InterruptedException {
        driver.get(BASE_URL);
        IndexPage indexPage = new IndexPage(driver);
        ChatBotPage chatBotPage = indexPage.clickChatBotButton();

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "chatbot"));

        String prompt = "Eu preciso de uma cadeira ergonômica";
        String userMessage = chatBotPage.keyPrompt(prompt);
        Assertions.assertEquals("Eu preciso de uma cadeira ergonômica", userMessage);
        chatBotPage.clickSendButton();
        String userMessageResponse = chatBotPage.getLastUserMessage();
        Assertions.assertEquals(prompt, userMessageResponse);
        Thread.sleep(2000);
        List<WebElement> botMessage = chatBotPage.getLastBotMessage();
        Thread.sleep(2000);
        Assertions.assertNotNull(botMessage, "Bot message should not be null");
        Assertions.assertEquals(1, botMessage.size());
        Thread.sleep(2000);
        botMessage.getFirst().click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "chairs/1"));
        ChairPage chairPage = new ChairPage(driver);
        chairPage.accessHeaderOption(IndexPage.HeaderOptions.HOME);
        indexPage = new IndexPage(driver);
        Thread.sleep(2000);
        indexPage.clickChatBotButton();
        chatBotPage = new ChatBotPage(driver);
        String newPrompt = "Banana de chocolate com abacaxi";
        String newUserMessage = chatBotPage.keyPrompt(newPrompt);
        Assertions.assertEquals("Banana de chocolate com abacaxi", newUserMessage);
        chatBotPage.clickSendButton();
        String newUserMessageResponse = chatBotPage.getLastUserMessage();
        Assertions.assertEquals(newPrompt, newUserMessageResponse);
        Thread.sleep(2000);
        List<WebElement> newBotMessage = chatBotPage.getLastBotMessageAfter(2);
        Thread.sleep(2000);
        Assertions.assertNotNull(newBotMessage, "Bot message should not be null");
        Assertions.assertEquals(0, newBotMessage.size());
        String advice = chatBotPage.getAdviceOnLastBotMessage();
        Assertions.assertEquals("Desculpe, não consegui encontrar cadeiras para a sua solicitação.", advice);


    }

    @AfterEach
    public void setDown() {
        driver.quit();
        seeder.truncateAllTables();
    }

    @AfterAll
    public static void afterAll() {
        try {
            ContainerInitializer.down();
        } catch (Exception e) {
            throw new RuntimeException("Failed to stop containers", e);
        }
    }
}

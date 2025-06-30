package com.chairpick.ecommerce.e2e.pages.creditCards;

import com.chairpick.ecommerce.e2e.factories.WebDriverFactory;
import com.chairpick.ecommerce.e2e.pageObjects.creditCards.CreateCreditCardPage;
import com.chairpick.ecommerce.e2e.pageObjects.creditCards.CreditCardHomePage;
import com.chairpick.ecommerce.e2e.pageObjects.creditCards.EditCreditCardPage;
import com.chairpick.ecommerce.e2e.pageObjects.customers.ProfilePage;
import com.chairpick.ecommerce.e2e.pageObjects.index.IndexPage;
import com.chairpick.ecommerce.e2e.utils.ChairInitializer;
import com.chairpick.ecommerce.e2e.utils.ContainerInitializer;
import com.chairpick.ecommerce.e2e.utils.DatabaseSeeder;
import com.chairpick.ecommerce.e2e.utils.UsersInitializer;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CreditCardTest {

    @Autowired
    private DatabaseSeeder seeder;
    private final String BASE_URL = "http://localhost:8080/";
    private WebDriver driver;
    private WebDriverWait wait;
    private UsersInitializer usersInitializer;

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

        usersInitializer = new UsersInitializer(seeder);
        ChairInitializer chairInitializer = new ChairInitializer(seeder);
        usersInitializer.createDefaultAdminAndCustomer().authWithCustomer(driver, wait);
        chairInitializer.seedDefaultProducts();
    }

    @Test
    public void shouldCreateACreditCard() throws InterruptedException {
        driver.get(BASE_URL);
        IndexPage indexPage = new IndexPage(driver);
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.PROFILE);
        ProfilePage profilePage = new ProfilePage(driver);
        CreditCardHomePage creditCardHomePage = profilePage.accessCreditCardsPage();
        wait.until(ExpectedConditions.urlContains("/credit-cards"));
        CreateCreditCardPage createCreditCardPage = creditCardHomePage.goToCreateCreditCardPage();
        wait.until(ExpectedConditions.urlContains("/credit-cards/new"));
        String name = createCreditCardPage.fillName("John Doe");
        Assertions.assertEquals("JOHN DOE", name);
        String number = createCreditCardPage.fillNumber("1234567890123456");
        Assertions.assertEquals("1234 5678 9012 3456", number);
        String brand = createCreditCardPage.fillBrand("mastercard");
        Assertions.assertEquals("mastercard", brand);
        String cvv = createCreditCardPage.fillCvv("123");
        Assertions.assertEquals("123", cvv);
        Assertions.assertTrue(createCreditCardPage.isSubmitButtonEnabled(), "Submit button should be enabled");
        CreditCardHomePage homePage = createCreditCardPage.submit();
        wait.until(ExpectedConditions.urlContains("/credit-cards"));
        var lastRowValues = homePage.getValuesForLastRow();
        Assertions.assertEquals("1234 **** **** ****", lastRowValues.get("number"));
        Assertions.assertEquals("JOHN DOE", lastRowValues.get("name"));
        Assertions.assertEquals("MASTERCARD", lastRowValues.get("brand"));
        Assertions.assertEquals("123", lastRowValues.get("cvv"));
        Assertions.assertEquals("Não", lastRowValues.get("default"));
        Assertions.assertTrue(lastRowValues.get("edit") instanceof WebElement, "Edit button should be present");
        Thread.sleep(2000);
    }

    @Test
    public void shouldEditACreditCard() throws InterruptedException {
        driver.get(BASE_URL);
        IndexPage indexPage = new IndexPage(driver);
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.PROFILE);
        ProfilePage profilePage = new ProfilePage(driver);
        CreditCardHomePage creditCardHomePage = profilePage.accessCreditCardsPage();
        wait.until(ExpectedConditions.urlContains("/credit-cards"));
        var lastRowValues = creditCardHomePage.getValuesForLastRow();
        Assertions.assertFalse(lastRowValues.isEmpty(), "There should be at least one credit card");
        WebElement editButton = (WebElement) lastRowValues.get("edit");
        Assertions.assertNotNull(editButton, "Edit button should not be null");
        editButton.click();
        wait.until(ExpectedConditions.urlContains("/credit-cards/edit"));
        EditCreditCardPage editCreditCardPage = new EditCreditCardPage(driver);
        String newName = editCreditCardPage.changeName("Jonhson Doe");
        Assertions.assertEquals("JONHSON DOE", newName, "Credit card name should be updated");
        Assertions.assertTrue(editCreditCardPage.isSubmitButtonEnabled(), "Submit button should be enabled after name change");
        CreditCardHomePage homePage = editCreditCardPage.submit();
        wait.until(ExpectedConditions.urlContains("/credit-cards"));
        var updatedRowValues = homePage.getValuesForLastRow();
        Assertions.assertEquals("JONHSON DOE", updatedRowValues.get("name"), "Credit card name should be updated in the list");
        Thread.sleep(2000);
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

package com.chairpick.ecommerce.e2e.pages.address;

import com.chairpick.ecommerce.e2e.factories.WebDriverFactory;
import com.chairpick.ecommerce.e2e.pageObjects.addresses.AddressHomePage;
import com.chairpick.ecommerce.e2e.pageObjects.addresses.CreateAddressPage;
import com.chairpick.ecommerce.e2e.pageObjects.addresses.EditAddressPage;
import com.chairpick.ecommerce.e2e.pageObjects.customers.ProfilePage;
import com.chairpick.ecommerce.e2e.pageObjects.index.IndexPage;
import com.chairpick.ecommerce.e2e.utils.ChairInitializer;
import com.chairpick.ecommerce.e2e.utils.ContainerInitializer;
import com.chairpick.ecommerce.e2e.utils.DatabaseSeeder;
import com.chairpick.ecommerce.e2e.utils.UsersInitializer;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AddressTest {

    @Autowired
    private DatabaseSeeder seeder;
    private final String BASE_URL = "http://localhost:8080/";
    private WebDriver driver;
    private WebDriverWait wait;
    private UsersInitializer usersInitializer;


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
    public void shouldCreateAnAddress() throws InterruptedException {
        driver.get(BASE_URL);
        IndexPage indexPage = new IndexPage(driver);
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.PROFILE);
        ProfilePage profilePage = new ProfilePage(driver);
        AddressHomePage addressHomePage = profilePage.accessAddressesPage();
        CreateAddressPage createAddressPage = addressHomePage.goToCreateAddressPage();
        String addressName = createAddressPage.fillName("Test Address");
        Assertions.assertEquals("Test Address", addressName);
        String street = createAddressPage.fillStreet("123 Main St");
        Assertions.assertEquals("123 Main St", street);
        String number = createAddressPage.fillNumber("456");
        Assertions.assertEquals("456", number);
        String cep = createAddressPage.fillCep("12345678");
        Assertions.assertEquals("12345-678", cep);
        String neighborhood = createAddressPage.fillNeighborhood("Downtown");
        Assertions.assertEquals("Downtown", neighborhood);
        String city = createAddressPage.fillCity("Metropolis");
        Assertions.assertEquals("Metropolis", city);
        String state = createAddressPage.fillState("NY");
        Assertions.assertEquals("NY", state);
        String country = createAddressPage.fillCountry("USA");
        Assertions.assertEquals("USA", country);
        Assertions.assertTrue(createAddressPage.isSubmitButtonEnabled());
        addressHomePage = createAddressPage.submit();
        Map<String, Object> lastRowValues = addressHomePage.getValuesForLastRow();
        Assertions.assertEquals("Test Address", lastRowValues.get("name"));
        Assertions.assertEquals("123 Main St", lastRowValues.get("street"));
        Assertions.assertEquals("456", lastRowValues.get("number"));
        Assertions.assertEquals("12345-678", lastRowValues.get("cep"));
        Assertions.assertEquals("Downtown", lastRowValues.get("neighborhood"));
        Assertions.assertEquals("Metropolis", lastRowValues.get("city"));
        Assertions.assertEquals("NY", lastRowValues.get("state"));
        Assertions.assertEquals("USA", lastRowValues.get("country"));
        Assertions.assertEquals("", lastRowValues.get("observations"));
    }

    @Test
    public void shouldEditAnAddress() throws InterruptedException {
        driver.get(BASE_URL);
        IndexPage indexPage = new IndexPage(driver);
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.PROFILE);
        ProfilePage profilePage = new ProfilePage(driver);
        AddressHomePage addressHomePage = profilePage.accessAddressesPage();
        Map<String, Object> lastRowValues = addressHomePage.getValuesForLastRow();
        Assertions.assertFalse(lastRowValues.isEmpty(), "No addresses found to edit");
        Object editButton = lastRowValues.get("edit");
        Assertions.assertInstanceOf(WebElement.class, editButton);
        ((WebElement) editButton).click();
        EditAddressPage editAddressPage = new EditAddressPage(driver);
        String newName = editAddressPage.changeName("Updated Address");
        Assertions.assertEquals("Updated Address", newName);
        Assertions.assertTrue(editAddressPage.isSubmitButtonEnabled(), "Submit button should be enabled after editing");
        addressHomePage = editAddressPage.submit();
        Map<String, Object> updatedRowValues = addressHomePage.getValuesForLastRow();
        Assertions.assertEquals("Updated Address", updatedRowValues.get("name"), "Address name should be updated");
        Thread.sleep(2000);
    }

    @AfterEach
    public void setDown() {
        driver.quit();
        seeder.truncateAllTables();
    }
}

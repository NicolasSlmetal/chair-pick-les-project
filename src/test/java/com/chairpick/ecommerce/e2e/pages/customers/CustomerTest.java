package com.chairpick.ecommerce.e2e.pages.customers;


import com.chairpick.ecommerce.e2e.factories.WebDriverFactory;
import com.chairpick.ecommerce.e2e.pageObjects.components.ConfirmModal;
import com.chairpick.ecommerce.e2e.pageObjects.components.InfoModal;
import com.chairpick.ecommerce.e2e.pageObjects.customers.*;
import com.chairpick.ecommerce.e2e.pageObjects.index.IndexPage;
import com.chairpick.ecommerce.e2e.pageObjects.auth.LoginPage;
import com.chairpick.ecommerce.e2e.utils.ContainerInitializer;
import com.chairpick.ecommerce.e2e.utils.DatabaseSeeder;
import com.chairpick.ecommerce.e2e.utils.UsersInitializer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CustomerTest implements TestWatcher {

    @Autowired
    private DatabaseSeeder seeder;
    private WebDriver driver;
    private final String BASE_URL = "http://localhost:8080/";
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
    }

    @Test
    public void shouldCreateACustomer() {
        driver.get(BASE_URL);
        IndexPage indexPage = new IndexPage(driver);
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.LOGIN);
        LoginPage loginPage = new LoginPage(driver);
        CreateCustomerPage createCustomerPage = loginPage.goToCreateAccountPage();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "customers/new"));
        String name = createCustomerPage.fillName("John Doe");
        Assertions.assertEquals("John Doe", name);
        String cpf = createCustomerPage.fillCpf("91536735060");
        Assertions.assertEquals("915.367.350-60", cpf);
        String email = createCustomerPage.fillEmail("johndoe@email.com");
        Assertions.assertEquals("johndoe@email.com", email);
        String phone = createCustomerPage.fillPhone("11999999999");
        Assertions.assertEquals("(11) 99999-9999", phone);
        String bornDate = createCustomerPage.fillBornDate("1990-01-01");
        Assertions.assertEquals("1990-01-01", bornDate);
        String genre = createCustomerPage.fillGenre("male");
        Assertions.assertEquals("male", genre);
        String addressName = createCustomerPage.fillAddressName("Home");
        Assertions.assertEquals("Home", addressName);
        String addressCep = createCustomerPage.fillAddressCep("12345678");
        Assertions.assertEquals("12345-678", addressCep);
        String addressStreet = createCustomerPage.fillAddressStreet("123 Main St");
        Assertions.assertEquals("123 Main St", addressStreet);
        String addressNumber = createCustomerPage.fillAddressNumber("123");
        Assertions.assertEquals("123", addressNumber);
        String addressNeighborhood = createCustomerPage.fillAddressNeighborhood("Downtown");
        Assertions.assertEquals("Downtown", addressNeighborhood);
        String addressCity = createCustomerPage.fillAddressCity("Metropolis");
        Assertions.assertEquals("Metropolis", addressCity);
        String addressState = createCustomerPage.fillAddressState("SP");
        Assertions.assertEquals("SP", addressState);
        String addressCountry = createCustomerPage.fillAddressCountry("Brazil");
        Assertions.assertEquals("Brazil", addressCountry);
        createCustomerPage.submitAddress();
        String creditCardNumber = createCustomerPage.fillCreditCardNumber("4111111111111111");
        Assertions.assertEquals("4111 1111 1111 1111", creditCardNumber);
        String creditCardHolderName = createCustomerPage.fillCreditCardName("John Doe");
        Assertions.assertEquals("JOHN DOE", creditCardHolderName);
        String cvv = createCustomerPage.fillCreditCardCvv("123");
        Assertions.assertEquals("123", cvv);
        createCustomerPage.submitCreditCard();
        String password = createCustomerPage.fillPassword("Customer123!");
        Assertions.assertEquals("Customer123!", password);
        String confirmPassword = createCustomerPage.fillPasswordConfirm("Customer123!");
        Assertions.assertEquals("Customer123!", confirmPassword);
        createCustomerPage.submit();
        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.PROFILE);

        ProfilePage profilePage = new ProfilePage(driver);

        String profileName = profilePage.getCustomerName();
        Assertions.assertEquals("John Doe", profileName);
        String profileEmail = profilePage.getCustomerEmail();
        Assertions.assertTrue(profileEmail.contains("johndoe@email.com"));
        String profileCpf = profilePage.getCustomerCpf();
        Assertions.assertTrue(profileCpf.contains("915.367.350-60"));
        String profilePhone = profilePage.getCustomerPhone();
        Assertions.assertTrue(profilePhone.contains("(11) 99999-9999"));
        String profilePhoneType = profilePage.getCustomerPhoneType();
        Assertions.assertTrue(profilePhoneType.contains("Celular"));

        //Should be able to logout and login again
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.LOGOUT);
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.LOGIN);
        loginPage = new LoginPage(driver);

        loginPage.fillEmail("johndoe@email.com");
        loginPage.fillPassword("Customer123!");
        Assertions.assertTrue(loginPage.isSubmitButtonEnabled());
        loginPage.submit();
        wait.until(ExpectedConditions.not(ExpectedConditions.titleIs("Login")));
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.PROFILE);
        profilePage = new ProfilePage(driver);
        String loggedInProfileName = profilePage.getCustomerName();
        Assertions.assertEquals("John Doe", loggedInProfileName);
    }

    @Test
    public void shouldUpdateACustomer() throws InterruptedException {
        usersInitializer.createDefaultAdminAndCustomer().authWithCustomer(driver, wait);
        driver.get(BASE_URL);
        IndexPage indexPage = new IndexPage(driver);
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.PROFILE);
        ProfilePage profilePage = new ProfilePage(driver);
        EditCustomerPage editCustomerPage = profilePage.accessEditCustomerPage();
        wait.until(ExpectedConditions.urlContains("/edit"));
        String name = editCustomerPage.changeName("Johnson Doe");
        Assertions.assertEquals("Johnson Doe", name);
        editCustomerPage.submit();
        Thread.sleep(2000);
        String profileName = profilePage.getCustomerName();
        Assertions.assertEquals("Johnson Doe", profileName);

    }

    @Test
    public void shouldInactivateACustomer() {
        usersInitializer.createDefaultAdminAndCustomer().authWithCustomer(driver, wait);
        driver.get(BASE_URL);
        IndexPage indexPage = new IndexPage(driver);
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.PROFILE);
        ProfilePage profilePage = new ProfilePage(driver);
        ConfirmModal modal = profilePage.clickToDeleteAcount();
        modal.confirm();
        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        //Try to login with the inactivated customer
        Assertions.assertThrows(RuntimeException.class, () -> usersInitializer.authWithCustomer(driver, wait));
    }

    @Test
    public void shouldAlterCustomerPassword() {
        usersInitializer.createDefaultAdminAndCustomer().authWithCustomer(driver, wait);
        driver.get(BASE_URL);
        IndexPage indexPage = new IndexPage(driver);
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.PROFILE);
        ProfilePage profilePage = new ProfilePage(driver);
        CustomerAlterPasswordPage alterPasswordPage = profilePage.accessEditPasswordPage();
        wait.until(ExpectedConditions.urlContains("/alter-password"));
        String newPassword = alterPasswordPage.fillPassword("NewCustomer123!");
        Assertions.assertEquals("NewCustomer123!", newPassword);
        String confirmPassword = alterPasswordPage.fillPasswordConfirmation("NewCustomer123!");
        Assertions.assertEquals("NewCustomer123!", confirmPassword);
        InfoModal modal = alterPasswordPage.submit();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("dialog")));
        Assertions.assertEquals("Senha alterada", modal.getTitle());
        Assertions.assertEquals("Você já pode fazer login com a nova senha.", modal.getMessage());
        modal.clickOkButton();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "customers/1"));
        profilePage.accessHeaderOption(IndexPage.HeaderOptions.LOGOUT);
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.LOGIN);
        LoginPage loginPage = new LoginPage(driver);
        String email = loginPage.fillEmail("customer@email.com");
        Assertions.assertEquals("customer@email.com", email);
        String password = loginPage.fillPassword("NewCustomer123!");
        Assertions.assertEquals("NewCustomer123!", password);
        loginPage.submit();
        wait.until(ExpectedConditions.not(ExpectedConditions.titleIs("Login")));
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.PROFILE);
        email = profilePage.getCustomerEmail();
        Assertions.assertEquals("Email: customer@email.com", email);
    }

    @Test
    public void shouldSearchForCustomers() throws InterruptedException {
        usersInitializer.createDefaultAdminAndCustomer().authWithAdmin(driver, wait);
        CustomerHomePage customerHomePage = new CustomerHomePage(driver);
        customerHomePage.openSearchMenu();
        String searchName = customerHomePage.searchForName("John Doe");
        Assertions.assertEquals("John Doe", searchName);
        customerHomePage.submitSearch();
        Thread.sleep(2000);
        var values = customerHomePage.getValuesForLastRow();
        Assertions.assertEquals("1", values.get("rank"));
        Assertions.assertEquals("John Doe", values.get("name"));
        Assertions.assertEquals("915.367.350-60", values.get("cpf"));
        Assertions.assertEquals("(11) 99999-9999 (Celular)", values.get("phone"));
        Assertions.assertEquals("Masculino", values.get("genre"));
        Assertions.assertEquals("01/01/1990", values.get("birthdate"));

        customerHomePage.openSearchMenu();
        searchName = customerHomePage.searchForName("Johnson Doe");
        Assertions.assertEquals("Johnson Doe", searchName);
        customerHomePage.submitSearch();
        Thread.sleep(2000);
        values = customerHomePage.getValuesForLastRow();
        Assertions.assertTrue(values.isEmpty());
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

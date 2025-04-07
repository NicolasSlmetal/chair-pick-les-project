package com.chairpick.ecommerce.e2e.pages.customers;


import com.chairpick.ecommerce.e2e.factories.WebDriverFactory;
import com.chairpick.ecommerce.e2e.pageObjects.addresses.AddressHomePage;
import com.chairpick.ecommerce.e2e.pageObjects.creditCards.CreditCardHomePage;
import com.chairpick.ecommerce.e2e.pageObjects.customers.CreateCustomerPage;
import com.chairpick.ecommerce.e2e.pageObjects.customers.CustomerAlterPasswordPage;
import com.chairpick.ecommerce.e2e.pageObjects.customers.CustomerHomePage;
import com.chairpick.ecommerce.e2e.pageObjects.customers.EditCustomerPage;
import com.chairpick.ecommerce.model.enums.PhoneType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CustomerTest implements TestWatcher {

    private static final String DIRNAME = System.getProperty("user.dir");

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    private final String BASE_URL = "http://localhost:8080/customers";
    private WebDriver driver;
    private WebDriverWait wait;


    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        cause.printStackTrace();
        driver.quit();
        try {
            downContainer();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertCustomerBeforeStart() {
        String password = System.getenv("PASSWORD");
        password = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO tb_user (usr_email, usr_password, usr_type) VALUES (:email, :password, :type) RETURNING usr_id";
        Long userId = jdbcTemplate.queryForObject(sql, Map.of(
                "email", "johndoe@email.com",
                "password", password,
                "type", "CUSTOMER"), Long.class);

        if (userId == null) {
            throw new IllegalStateException("Could not create the user");
        }


        LocalDate date = LocalDate.of(2005, 3, 14);
        sql = "INSERT INTO tb_customer (cus_name, cus_cpf, cus_phone, cus_phone_ddd, cus_phone_type, cus_genre, cus_born_date, cus_user_id, cus_active) VALUES (:name, :cpf, :phone, :ddd, :phoneType, :genre, :birthdate, :userId, :active)";
        jdbcTemplate.update(sql, Map.of(
                "name", "John Doe",
                "cpf", "91536735060",
                "phone", "999999999",
                "ddd", "11",
                "phoneType", PhoneType.CELL_PHONE.name(),
                "genre", "M",
                "birthdate", date,
                "userId", userId,
                "active", 1));
    }

    private void truncateTables() {
        jdbcTemplate.update("TRUNCATE TABLE tb_customer CASCADE", Map.of());
        jdbcTemplate.update("TRUNCATE TABLE tb_user CASCADE", Map.of());
    }

    @BeforeAll
    public static void up() throws IOException, InterruptedException {
        Process process = new ProcessBuilder("docker", "compose", "-f", "docker-compose-test.yaml", "up", "-d")
                .directory(new File(DIRNAME))
                .start();
        process.waitFor();

        if (process.exitValue() != 0) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            throw new IllegalStateException("Could not start the application");
        }
    }

    @BeforeEach
    public void setUp() {
        driver = WebDriverFactory.createWebDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
        truncateTables();
    }

    @Test
    public void shouldCreateACustomer() {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));

        CustomerHomePage customerHomePage = new CustomerHomePage(driver);
        CreateCustomerPage createCustomerPage = customerHomePage.accessCreateCustomerPage();

        Assertions.assertEquals(BASE_URL + "/new", driver.getCurrentUrl());

        String name = "John Doe";
        String expectedCpf = "915.367.350-60";
        String expectedEmail = "johndoe@email.com";
        String expectedPhone = "(11) 99999-9999";
        String expectedBornDate = "2005-03-14";
        String expectedPhoneType = "cell_phone";
        String expectedGenre = "male";
        String expectedAddressName = "House";
        String expectedCep = "00000-000";
        String expectedStreet = "Street";
        String expectedCreditCardCvv = "123";
        String expectedAddressNumber = "123";
        String expectedAddressNeighborhood = "Neighborhood";
        String expectedAddressType = "street";
        String expectedAddressCity = "City";
        String expectedAddressState = "State";
        String expectedCountry = "Country";
        String expectedObservation = "Observation";
        String expectedCreditCardNumber = "1234 5678 9012 3456";
        String envPassword = System.getenv("PASSWORD");
        String expectedPassword = envPassword != null && !envPassword.isEmpty() ?  envPassword : "Abcdedf12345@";

        Assertions.assertEquals(name, createCustomerPage.fillName(name));
        Assertions.assertEquals(expectedCpf, createCustomerPage.fillCpf("91536735060"));
        Assertions.assertEquals(expectedEmail, createCustomerPage.fillEmail(expectedEmail));
        Assertions.assertEquals(expectedPhone, createCustomerPage.fillPhone("11999999999"));
        Assertions.assertEquals(expectedBornDate, createCustomerPage.fillBornDate(expectedBornDate));
        Assertions.assertEquals(expectedPhoneType, createCustomerPage.fillPhoneType(expectedPhoneType));
        Assertions.assertEquals(expectedGenre, createCustomerPage.fillGenre(expectedGenre));
        Assertions.assertEquals(expectedAddressName, createCustomerPage.fillAddressName(expectedAddressName));
        Assertions.assertEquals(expectedCep, createCustomerPage.fillAddressCep("00000000"));
        Assertions.assertEquals(expectedStreet, createCustomerPage.fillAddressStreet(expectedStreet));
        Assertions.assertEquals(expectedAddressNumber, createCustomerPage.fillAddressNumber(expectedAddressNumber));
        Assertions.assertEquals(expectedAddressNeighborhood, createCustomerPage.fillAddressNeighborhood(expectedAddressNeighborhood));
        Assertions.assertEquals(expectedAddressType, createCustomerPage.fillAddressStreetType(expectedAddressType));
        Assertions.assertEquals(expectedAddressCity, createCustomerPage.fillAddressCity(expectedAddressCity));
        Assertions.assertEquals(expectedAddressState, createCustomerPage.fillAddressState(expectedAddressState));
        Assertions.assertEquals(expectedCountry, createCustomerPage.fillAddressCountry(expectedCountry));
        Assertions.assertEquals(expectedObservation, createCustomerPage.fillAddressObservations(expectedObservation));

        createCustomerPage.submitAddress();

        Assertions.assertFalse(createCustomerPage.getCardsFromAddressSection().isEmpty());

        Assertions.assertEquals(expectedCreditCardNumber, createCustomerPage.fillCreditCardNumber("1234567890123456"));
        Assertions.assertEquals(name.toUpperCase(), createCustomerPage.fillCreditCardName(name));
        Assertions.assertEquals(expectedCreditCardCvv, createCustomerPage.fillCreditCardCvv(expectedAddressNumber));

        createCustomerPage.submitCreditCard();

        Assertions.assertFalse(createCustomerPage.getCardsFromCreditCardSection().isEmpty());

        Assertions.assertEquals(expectedPassword, createCustomerPage.fillPassword(expectedPassword));
        Assertions.assertEquals(expectedPassword, createCustomerPage.fillPasswordConfirm(expectedPassword));

        createCustomerPage.submit();
        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        Assertions.assertEquals(BASE_URL, driver.getCurrentUrl());

        String expectedResultPhone = "(11) 99999-9999 (Celular)";
        String expectedResultGenre = "Masculino";
        String expectedBornDateResult = "14/03/2005";

        Map<String, Object> valuesForLastRow = customerHomePage.getValuesForLastRow();
        Assertions.assertEquals(name, valuesForLastRow.get("name"));
        Assertions.assertEquals(expectedCpf, valuesForLastRow.get("cpf"));
        Assertions.assertEquals(expectedResultPhone, valuesForLastRow.get("phone"));
        Assertions.assertEquals(expectedResultGenre, valuesForLastRow.get("genre"));
        Assertions.assertEquals(expectedBornDateResult, valuesForLastRow.get("birthdate"));
        Assertions.assertEquals(expectedEmail, valuesForLastRow.get("email"));
        Assertions.assertNotNull(valuesForLastRow.get("actions"));
        Assertions.assertNotNull(valuesForLastRow.get("edit"));
        Assertions.assertNotNull(valuesForLastRow.get("delete"));

        WebElement actionsButton = (WebElement) valuesForLastRow.get("actions");
        Map<String, WebElement> actions = customerHomePage.openActionsForRow(actionsButton);
        CreditCardHomePage creditCardHomePage = (CreditCardHomePage) customerHomePage.openOptionOfActionsButton(actions.get("creditCards"), "creditCards");
        String expectedUrl = String.format("%s/([1-9]+)/credit-cards", BASE_URL);
        wait.until(ExpectedConditions.urlMatches(expectedUrl));
        Assertions.assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).matches(expectedUrl));

        Map<String, Object> creditCardsLastRow = creditCardHomePage.getValuesForLastRow();
        Assertions.assertFalse(creditCardsLastRow.isEmpty());
        String expectedHiddenCreditCardNumber = "1234 **** **** ****";

        Assertions.assertEquals(expectedHiddenCreditCardNumber, creditCardsLastRow.get("number"));
        Assertions.assertEquals(name.toUpperCase(), creditCardsLastRow.get("name"));
        Assertions.assertEquals("VISA", creditCardsLastRow.get("brand"));
        Assertions.assertEquals(expectedCreditCardCvv, creditCardsLastRow.get("cvv"));
        Assertions.assertEquals("Sim", creditCardsLastRow.get("default"));
        Assertions.assertNotNull(creditCardsLastRow.get("edit"));
        Assertions.assertNotNull(creditCardsLastRow.get("delete"));

        creditCardHomePage.back();
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        Assertions.assertEquals(BASE_URL, driver.getCurrentUrl());

        valuesForLastRow = customerHomePage.getValuesForLastRow();
        actionsButton = (WebElement) valuesForLastRow.get("actions");
        actions = customerHomePage.openActionsForRow(actionsButton);
        AddressHomePage addressHomePage = (AddressHomePage) customerHomePage.openOptionOfActionsButton(actions.get("addresses"), "addresses");

        expectedUrl = String.format("%s/([1-9]+)/addresses", BASE_URL);
        wait.until(ExpectedConditions.urlMatches(expectedUrl));
        Assertions.assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).matches(expectedUrl));

        Map<String, Object> addressLastRow = addressHomePage.getValuesForLastRow();

        Assertions.assertFalse(addressLastRow.isEmpty());
        Assertions.assertEquals(expectedAddressName, addressLastRow.get("name"));
        Assertions.assertEquals(expectedStreet, addressLastRow.get("street"));
        Assertions.assertEquals("Rua", addressLastRow.get("streetType"));
        Assertions.assertEquals(expectedCep, addressLastRow.get("cep"));
        Assertions.assertEquals(expectedAddressNeighborhood, addressLastRow.get("neighborhood"));
        Assertions.assertEquals(expectedAddressNumber, addressLastRow.get("number"));
        Assertions.assertEquals(expectedAddressCity, addressLastRow.get("city"));
        Assertions.assertEquals(expectedAddressState, addressLastRow.get("state"));
        Assertions.assertEquals(expectedCountry, addressLastRow.get("country"));
        Assertions.assertEquals(expectedObservation, addressLastRow.get("observations"));
        Assertions.assertNotNull(addressLastRow.get("edit"));
        Assertions.assertNotNull(addressLastRow.get("delete"));
    }

    @Test
    public void shouldEditACustomer() {
        insertCustomerBeforeStart();
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));

        CustomerHomePage customerHomePage = new CustomerHomePage(driver);
        Map<String, Object> valuesForLastRow = customerHomePage.getValuesForLastRow();

        Assertions.assertNotNull(valuesForLastRow);
        WebElement editButton = (WebElement) valuesForLastRow.get("edit");
        EditCustomerPage editCustomerPage = customerHomePage.editRow(editButton);

        String expectedUrl = String.format("%s/edit/([1-9]+)", BASE_URL);
        Assertions.assertTrue(driver.getCurrentUrl().matches(expectedUrl));

        Assertions.assertEquals("John Doe", editCustomerPage.getValueForName());
        Assertions.assertEquals("915.367.350-60", editCustomerPage.getValueForCpf());
        Assertions.assertEquals("johndoe@email.com", editCustomerPage.getValueForEmail());
        Assertions.assertEquals("(11) 99999-9999", editCustomerPage.getValueForPhone());
        Assertions.assertEquals("CELL_PHONE", editCustomerPage.getValueForPhoneType());
        Assertions.assertEquals("MALE", editCustomerPage.getValueForGenre());
        Assertions.assertEquals("2005-03-14", editCustomerPage.getValueForBornDate());

        Assertions.assertEquals("John Doe Kane", editCustomerPage.changeName("John Doe Kane"));

        editCustomerPage.submit();

        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        Assertions.assertEquals(BASE_URL, driver.getCurrentUrl());

        Map<String, Object> valuesForLastRowAfterEdit = customerHomePage.getValuesForLastRow();
        Assertions.assertNotNull(valuesForLastRowAfterEdit);
        Assertions.assertEquals("John Doe Kane", valuesForLastRowAfterEdit.get("name"));
    }

    @Test
    public void shouldSearchForAUser() {
        insertCustomerBeforeStart();
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));

        CustomerHomePage customerHomePage = new CustomerHomePage(driver);
        Map<String, Object> valuesForLastRow = customerHomePage.getValuesForLastRow();

        Assertions.assertNotNull(valuesForLastRow);
        String name = (String) valuesForLastRow.get("name");
        customerHomePage.openSearchMenu();
        customerHomePage.searchForName(name);
        customerHomePage.submitSearch();

        Map<String, Object> valuesForLastRowAfterSearch = customerHomePage.getValuesForLastRow();
        Assertions.assertNotNull(valuesForLastRowAfterSearch);
        Assertions.assertEquals(name, valuesForLastRowAfterSearch.get("name"));

        customerHomePage.openSearchMenu();
        customerHomePage.searchForName("Noah Kane");
        customerHomePage.submitSearch();

        valuesForLastRowAfterSearch = customerHomePage.getValuesForLastRow();
        Assertions.assertTrue(valuesForLastRowAfterSearch.isEmpty());
    }

    @Test
    public void shouldDeleteACustomer() {
        insertCustomerBeforeStart();
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));

        CustomerHomePage customerHomePage = new CustomerHomePage(driver);
        Map<String, Object> valuesForLastRow = customerHomePage.getValuesForLastRow();

        Assertions.assertNotNull(valuesForLastRow);
        WebElement deleteButton = (WebElement) valuesForLastRow.get("delete");

        customerHomePage.deleteRow(deleteButton);
        Assertions.assertEquals("Tem certeza que deseja remover o cliente John Doe?", customerHomePage.getTextFromDeleteConfirmDialog());
        customerHomePage.cancelDelete();

        valuesForLastRow = customerHomePage.getValuesForLastRow();
        Assertions.assertFalse(valuesForLastRow.isEmpty());

        customerHomePage.deleteRow(deleteButton);
        customerHomePage.confirmDelete();

        valuesForLastRow = customerHomePage.getValuesForLastRow();
        Assertions.assertTrue(valuesForLastRow.isEmpty());
    }

    @Test
    public void shouldAlterCustomerPassword() {
        insertCustomerBeforeStart();
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));

        CustomerHomePage customerHomePage = new CustomerHomePage(driver);
        Map<String, Object> valuesForLastRow = customerHomePage.getValuesForLastRow();
        Assertions.assertFalse(valuesForLastRow.isEmpty());

        WebElement actionsButton = (WebElement) valuesForLastRow.get("actions");
        Map<String, WebElement> actions = customerHomePage.openActionsForRow(actionsButton);
        CustomerAlterPasswordPage alterPasswordPage = (CustomerAlterPasswordPage) customerHomePage.openOptionOfActionsButton(actions.get("alterPassword"), "alterPassword");

        wait.until(ExpectedConditions.urlMatches(BASE_URL + "/([1-9]+)/alter-password"));
        String expectedUrl = String.format("%s/([1-9]+)/alter-password", BASE_URL);
        Assertions.assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).matches(expectedUrl));

        String newPassword = "Abcdedf12345@";

        Assertions.assertEquals("Abcdedf12345@", alterPasswordPage.fillPassword(newPassword));
        Assertions.assertEquals("Abcdedf12345@", alterPasswordPage.fillPasswordConfirmation(newPassword));
        alterPasswordPage.submit();

        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        Assertions.assertEquals(BASE_URL, driver.getCurrentUrl());

        Map<String, Object> valuesForLastRowAfterEdit = customerHomePage.getValuesForLastRow();
        Assertions.assertFalse(valuesForLastRowAfterEdit.isEmpty());
    }

    @AfterAll
    public static void down() throws IOException, InterruptedException {
        downContainer();
    }

    private static void downContainer() throws IOException, InterruptedException {
        Process process = new ProcessBuilder("docker", "compose", "-f", "docker-compose-test.yaml", "down", "-v")
                .directory(new File(DIRNAME))
                .start();
        process.waitFor();

        if (process.exitValue() != 0) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            throw new IllegalStateException("Could not stop the application");
        }
    }

}

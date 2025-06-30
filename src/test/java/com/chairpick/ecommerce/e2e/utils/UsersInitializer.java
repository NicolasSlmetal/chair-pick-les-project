package com.chairpick.ecommerce.e2e.utils;

import com.chairpick.ecommerce.e2e.pageObjects.auth.LoginPage;
import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.model.User;
import com.chairpick.ecommerce.model.enums.Genre;
import com.chairpick.ecommerce.model.enums.PhoneType;
import com.chairpick.ecommerce.model.enums.StreetType;
import com.chairpick.ecommerce.model.enums.UserType;
import com.chairpick.ecommerce.security.AuthenticatedUser;
import com.chairpick.ecommerce.services.TokenService;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UsersInitializer {

    private final DatabaseSeeder seeder;
    private final Map<UserType, User> users = new HashMap<>();
    private final Map<Long, Customer> customers = new HashMap<>();

    public UsersInitializer(DatabaseSeeder seeder) {
        this.seeder = seeder;
    }

    public UsersInitializer createDefaultAdminAndCustomer() {
        String adminQuery = "INSERT INTO tb_user (usr_email, usr_password, usr_type) VALUES (:username, :password, :role) RETURNING usr_id;";
        String userCustomerQuery = "INSERT INTO tb_user (usr_email, usr_password, usr_type) VALUES (:username, :password, :role) RETURNING usr_id;";
        String hashedCustomerPassword = BCrypt.hashpw("Customer123!", BCrypt.gensalt());
        String hashedAdminPassword = BCrypt.hashpw("Admin123!", BCrypt.gensalt());

        Long adminId = seeder.executeReturningId(adminQuery, Map.of("username", "admin@email.com", "password", hashedAdminPassword, "role", "ADMIN"));
        Long userCustomerId = seeder.executeReturningId(userCustomerQuery, Map.of("username", "customer@email.com", "password", hashedCustomerPassword, "role", "CUSTOMER"));

        String customerQuery = """
               INSERT INTO tb_customer (cus_name, cus_cpf, cus_phone, cus_phone_ddd, cus_phone_type, cus_genre, cus_born_date, cus_user_id, cus_active) VALUES (:name, :cpf, :phone, :ddd, :phoneType, :genre, :birthdate, :userId, :active)
               RETURNING cus_id;
        """;
        LocalDate date = LocalDate.of(1990, 1, 1);

        Map<String, Object> parameters = Map.of(
                "name", "John Doe",
                "cpf", "91536735060",
                "phone", "999999999",
                "ddd", "11",
                "phoneType", PhoneType.CELL_PHONE.name(),
                "genre", "M",
                "birthdate", date,
                "userId", userCustomerId,
                "active", 1);
        Long customerId = seeder.executeReturningId(customerQuery, parameters);
        users.put(UserType.CUSTOMER, User
                .builder()
                .id(userCustomerId)
                        .type(UserType.CUSTOMER)
                        .email("customer@email.com")
                        .password("Customer123!")
                .build());
        users.put(UserType.ADMIN, User
                .builder()
                        .id(adminId)
                        .type(UserType.ADMIN)
                        .email("admin@email.com")
                        .password("Admin123!")
                .build()
        );
        customers.put(userCustomerId, Customer
                .builder()
                        .id(customerId)
                        .name("John Doe")
                        .cpf("91536735060")
                        .phone("999999999")
                        .phoneDDD("11")
                        .phoneType(PhoneType.CELL_PHONE)
                        .genre(Genre.MALE)
                        .bornDate(date)
                        .user(users.get(UserType.CUSTOMER))
                .build());
        insertAddressAndCardForCustomer(customerId);
        return this;
    }

    public UsersInitializer createDefaultAdminAndSalesManager() {
        String adminQuery = "INSERT INTO tb_user (usr_email, usr_password, usr_type) VALUES (:username, :password, :role) RETURNING usr_id;";
        String userSalesManagerQuery = "INSERT INTO tb_user (usr_email, usr_password, usr_type) VALUES (:username, :password, :role) RETURNING usr_id;";
        String hashedSalesManagerPassword = BCrypt.hashpw("SalesManager123!", BCrypt.gensalt());
        String hashedAdminPassword = BCrypt.hashpw("Admin123!", BCrypt.gensalt());

        Map<String, Object> adminParams = Map.of(
                "username", "admin@email.com",
                "password", hashedAdminPassword,
                "role", "ADMIN"
        );
        Map<String, Object> salesManagerParams = Map.of(
            "username", "sales@email.com",
            "password",  hashedSalesManagerPassword,
            "role", "SALES_MANAGER"
        );

        Long adminId = seeder.executeReturningId(adminQuery, adminParams);
        Long salesManagerId = seeder.executeReturningId(userSalesManagerQuery, salesManagerParams);

        users.put(UserType.SALES_MANAGER, User.builder()
                        .email("sales@email.com")
                        .id(salesManagerId)
                        .password("SalesManager123!")
                        .type(UserType.SALES_MANAGER)
                .build());
        users.put(UserType.ADMIN, User
                .builder()
                .id(adminId)
                .type(UserType.ADMIN)
                .email("admin@email.com")
                .password("Admin123!")
                .build());
        return this;
    }

    private void insertAddressAndCardForCustomer(Long customerId) {
        String addressQuery = """
                INSERT INTO tb_address (add_street, add_default, add_name, add_street_type, add_number, add_observation, add_neighborhood, add_city, add_state, add_cep, add_country, add_customer_id)
                VALUES (:street, 1, :name, :streetType, :number, :observation, :neighborhood, :city, :state, :cep, :country, :customerId);
                """;
        String cardQuery = """
                INSERT INTO tb_credit_card (cre_number, cre_default, cre_cvv, cre_credit_brand, cre_holder,   cre_customer_id)
                VALUES (:number, 1, :cvv, :brandId, :holder, :customerId);
                """;
        Map<String, Object> addressParams = getAddressParams(customerId);

        Map<String, Object> creditCardParams = Map.of(
                "number", "1234123412341234",
                "cvv", "123",
                "brandId", 1,
                "holder", "Holder Name",
                "customerId", customerId
        );
        seeder.execute(addressQuery, addressParams);
        seeder.execute(cardQuery, creditCardParams);
    }

    private static Map<String, Object> getAddressParams(Long customerId) {
        Map<String, Object> addressParams = new HashMap<>();
        addressParams.put("street", "Street Name");
        addressParams.put("name", "Address Name");
        addressParams.put("streetType", StreetType.STREET.name());
        addressParams.put("number", 123);
        addressParams.put("observation", "Address Observation");
        addressParams.put("neighborhood", "Neighborhood");
        addressParams.put("city", "City");
        addressParams.put("state", "State");
        addressParams.put("cep", "12345678");
        addressParams.put("country", "Country");
        addressParams.put("customerId", customerId);
        return addressParams;
    }

    public void authWithCustomer(WebDriver driver, WebDriverWait wait) {
        driver.get("http://localhost:8080/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.fillEmail(users.get(UserType.CUSTOMER).getEmail());
        loginPage.fillPassword(users.get(UserType.CUSTOMER).getPassword());
        loginPage.submit();
        wait.until(ExpectedConditions.not(ExpectedConditions.titleIs("Login")));
    }

    public void authWithAdmin(WebDriver driver, WebDriverWait wait) {
        driver.get("http://localhost:8080/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.fillEmail(users.get(UserType.ADMIN).getEmail());
        loginPage.fillPassword(users.get(UserType.ADMIN).getPassword());
        loginPage.submit();
        wait.until(ExpectedConditions.not(ExpectedConditions.titleIs("Login")));
    }

    public void authWithSalesManager(WebDriver driver, WebDriverWait wait) {
        driver.get("http://localhost:8080/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.fillEmail(users.get(UserType.SALES_MANAGER).getEmail());
        loginPage.fillPassword(users.get(UserType.SALES_MANAGER).getPassword());
        loginPage.submit();
        wait.until(ExpectedConditions.not(ExpectedConditions.titleIs("Login")));
    }

    public Long getCustomerId() {
        return customers.values().stream().findFirst().map(Customer::getId).orElse(null);
    }


}

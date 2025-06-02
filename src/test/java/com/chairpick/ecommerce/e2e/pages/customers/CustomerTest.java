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



}

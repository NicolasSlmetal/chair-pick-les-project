package com.chairpick.ecommerce.e2e.pages.cart;

import com.chairpick.ecommerce.e2e.factories.WebDriverFactory;
import com.chairpick.ecommerce.e2e.pageObjects.cart.CartConfirmationPage;
import com.chairpick.ecommerce.e2e.pageObjects.cart.CartPage;
import com.chairpick.ecommerce.e2e.pageObjects.chairs.ChairPage;
import com.chairpick.ecommerce.e2e.pageObjects.customers.ProfilePage;
import com.chairpick.ecommerce.e2e.pageObjects.index.IndexPage;
import com.chairpick.ecommerce.e2e.pageObjects.orders.OrderPaymentPage;
import com.chairpick.ecommerce.e2e.utils.ChairInitializer;
import com.chairpick.ecommerce.e2e.utils.ContainerInitializer;
import com.chairpick.ecommerce.e2e.utils.DatabaseSeeder;
import com.chairpick.ecommerce.e2e.utils.UsersInitializer;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CartTest {


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
    public void shouldAddProductToCartAndManageHim() throws InterruptedException {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));

        IndexPage indexPage = new IndexPage(driver);
        ChairPage chairPage = indexPage.selectAnyProduct();
        CartPage cartPage = chairPage.addToCart(wait);
        wait.until(ExpectedConditions.urlContains("cart"));
        Assertions.assertFalse(cartPage.getAllProductsInCart().isEmpty(), "Cart should not be empty after adding a product");
        var product = cartPage.getAllProductsInCart().getFirst();
        Assertions.assertEquals(1, cartPage.getQuantityOfProductInCart(product), "Product quantity should be 1 after adding to cart");
        cartPage.increaseQuantityOfProductInCart(product);
        Thread.sleep(2000);
        Assertions.assertEquals(2, cartPage.getQuantityOfProductInCart(product), "Product quantity should be 2 after increasing");
        cartPage.decreaseQuantityOfProductInCart(product);
        Thread.sleep(2000);
        Assertions.assertEquals(1, cartPage.getQuantityOfProductInCart(product), "Product quantity should be 1 after decreasing");
        Thread.sleep(2000);
        cartPage.removeProductButtonSelector(product);
        Thread.sleep(2000);
        Assertions.assertTrue(cartPage.getAllProductsInCart().isEmpty(), "Cart should be empty after removing the product");
        cartPage.accessHeaderOption(IndexPage.HeaderOptions.HOME);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        chairPage = indexPage.selectAnyProduct();
        cartPage = chairPage.addToCart(wait);
        wait.until(ExpectedConditions.urlContains("cart"));
        Assertions.assertFalse(cartPage.getAllProductsInCart().isEmpty(), "Cart should not be empty after adding a product");
        product = cartPage.getAllProductsInCart().getFirst();
        int maxQuantity = cartPage.getMaxQuantityOfProductInCart(product);
        Assertions.assertEquals(1, cartPage.getQuantityOfProductInCart(product), "Product quantity should be 1 after adding to cart");
        cartPage.decreaseQuantityOfProductInCart(product);
        Thread.sleep(2000);
        Assertions.assertEquals(1, cartPage.getQuantityOfProductInCart(product), "Product quantity should be 1 after decreasing to 1 to 0, but avoiding");
        cartPage.increaseUntil(product, maxQuantity);
        Thread.sleep(2000);
        cartPage.increaseQuantityOfProductInCart(product);
        Thread.sleep(2000);
        Assertions.assertEquals(maxQuantity, cartPage.getQuantityOfProductInCart(product), "Product quantity should be max after increasing to max");

        CartConfirmationPage cartConfirmationPage = cartPage.confirmCart();
        wait.until(ExpectedConditions.urlContains("cart/confirm"));

        Assertions.assertEquals("Subtotal: R$ 100,00", cartConfirmationPage.getSubtotalBuyText(), "Subtotal price should be R$ 100.00 after confirming the cart");
        Assertions.assertEquals("Frete: R$ 8,90", cartConfirmationPage.getFreightText(), "Shipping price should be R$ 8.90 after confirming the cart");
        Assertions.assertEquals("Total: R$ 108,90", cartConfirmationPage.getTotalText(), "Total price should be R$ 108.90 after confirming the cart");

        OrderPaymentPage orderPaymentPage = cartConfirmationPage.clickConfirmButton();
        wait.until(ExpectedConditions.urlContains("orders/payment"));
        ProfilePage profilePage = orderPaymentPage.confirmOrder();

        wait.until(ExpectedConditions.urlContains("customers/1"));
        Thread.sleep(2000);
        profilePage.scrollIntoSectionOfOrders(ProfilePage.OrdersStatus.PENDING);
        var card = profilePage.getCardFromSection(ProfilePage.OrdersStatus.PENDING, 1, 1);

        Assertions.assertEquals("10", card.getProductAmount());
        Assertions.assertEquals("R$ 10,00", card.getProductPrice());
        Assertions.assertEquals("R$ 8,90", card.getProductFreight());
        Assertions.assertEquals("R$ 100,00", card.getProductSubtotal());
        Assertions.assertEquals("R$ 108,90", card.getProductTotal());
    }

    @Test
    public void shouldManageMoreThanOneProduct() throws InterruptedException {
        ChairInitializer chairInitializer = new ChairInitializer(seeder);
        chairInitializer.seedNewChair();

        driver.get(BASE_URL);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        IndexPage indexPage = new IndexPage(driver);
        ChairPage chairPage = indexPage.selectProductOfPosition(0);
        CartPage cartPage = chairPage.addToCart(wait);
        wait.until(ExpectedConditions.urlContains("cart"));
        Assertions.assertFalse(cartPage.getAllProductsInCart().isEmpty(), "Cart should not be empty after adding a product");
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.HOME);
        chairPage = indexPage.selectProductOfPosition(1);
        cartPage = chairPage.addToCart(wait);
        wait.until(ExpectedConditions.urlContains("cart"));
        Assertions.assertFalse(cartPage.getAllProductsInCart().isEmpty(), "Cart should not be empty after adding a second product");
        var products = cartPage.getAllProductsInCart();
        Assertions.assertEquals(2, products.size(), "Cart should have 2 products after adding two different products");
        var firstProduct = products.get(0);
        var secondProduct = products.get(1);
        Assertions.assertEquals(1, cartPage.getQuantityOfProductInCart(firstProduct), "First product quantity should be 1 after adding to cart");
        Assertions.assertEquals(1, cartPage.getQuantityOfProductInCart(secondProduct), "Second product quantity should be 1 after adding to cart");
        cartPage.increaseQuantityOfProductInCart(firstProduct);
        Thread.sleep(2000);
        Assertions.assertEquals(2, cartPage.getQuantityOfProductInCart(firstProduct), "First product quantity should be 2 after increasing");
        cartPage.decreaseQuantityOfProductInCart(secondProduct);
        Thread.sleep(2000);
        Assertions.assertEquals(1, cartPage.getQuantityOfProductInCart(secondProduct), "Second product quantity should be 1 after decreasing to 0, but avoiding");
        cartPage.increaseUntil(secondProduct, 5);
        Thread.sleep(2000);
        cartPage.increaseQuantityOfProductInCart(secondProduct);
        Thread.sleep(2000);
        Assertions.assertEquals(6, cartPage.getQuantityOfProductInCart(secondProduct), "Second product quantity should be 5 after increasing to max");
        CartConfirmationPage cartConfirmationPage = cartPage.confirmCart();
        wait.until(ExpectedConditions.urlContains("cart/confirm"));

        Assertions.assertEquals("Subtotal: R$ 80,00", cartConfirmationPage.getSubtotalBuyText(), "Subtotal price should be R$ 600.00 after confirming the cart");
        Assertions.assertEquals("Frete: R$ 17,80", cartConfirmationPage.getFreightText(), "Shipping price should be R$ 8.90 after confirming the cart");
        Assertions.assertEquals("Total: R$ 97,80", cartConfirmationPage.getTotalText(), "Total price should be R$ 608.90 after confirming the cart");
        OrderPaymentPage orderPaymentPage = cartConfirmationPage.clickConfirmButton();
        wait.until(ExpectedConditions.urlContains("orders/payment"));
        ProfilePage profilePage = orderPaymentPage.confirmOrder();
        wait.until(ExpectedConditions.urlContains("customers/1"));
        Thread.sleep(2000);
        profilePage.scrollIntoSectionOfOrders(ProfilePage.OrdersStatus.PENDING);
        Thread.sleep(2000);
        var card = profilePage.getCardOfSection(ProfilePage.OrdersStatus.PENDING, 0, 0);
        Assertions.assertEquals("2", card.getProductAmount());
        Assertions.assertEquals("R$ 10,00", card.getProductPrice());
        Assertions.assertEquals("R$ 8,90", card.getProductFreight());
        Assertions.assertEquals("R$ 20,00", card.getProductSubtotal());
        Assertions.assertEquals("R$ 28,90", card.getProductTotal(), "Total price should be R$ 608.90 after confirming the cart with two products");
        var secondCard = profilePage.getCardOfSection(ProfilePage.OrdersStatus.PENDING, 0, 1);
        Assertions.assertEquals("6", secondCard.getProductAmount());
        Assertions.assertEquals("R$ 10,00", secondCard.getProductPrice());
        Assertions.assertEquals("R$ 8,90", secondCard.getProductFreight());
        Assertions.assertEquals("R$ 60,00", secondCard.getProductSubtotal());
        Assertions.assertEquals("R$ 68,90", secondCard.getProductTotal(), "Total price should be R$ 608.90 after confirming the cart with two products");

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

package com.chairpick.ecommerce.e2e.pages.swap;

import com.chairpick.ecommerce.e2e.factories.WebDriverFactory;
import com.chairpick.ecommerce.e2e.pageObjects.cart.CartConfirmationPage;
import com.chairpick.ecommerce.e2e.pageObjects.chairs.ChairPage;
import com.chairpick.ecommerce.e2e.pageObjects.components.ConfirmModal;
import com.chairpick.ecommerce.e2e.pageObjects.customers.ProfilePage;
import com.chairpick.ecommerce.e2e.pageObjects.index.IndexPage;
import com.chairpick.ecommerce.e2e.pageObjects.orders.*;
import com.chairpick.ecommerce.e2e.pageObjects.swap.SwapPage;
import com.chairpick.ecommerce.e2e.utils.ChairInitializer;
import com.chairpick.ecommerce.e2e.utils.ContainerInitializer;
import com.chairpick.ecommerce.e2e.utils.DatabaseSeeder;
import com.chairpick.ecommerce.e2e.utils.UsersInitializer;
import com.chairpick.ecommerce.model.enums.OrderStatus;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SwapTest {

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
    public void shouldSwapAChair() throws InterruptedException {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));

        IndexPage indexPage = new IndexPage(driver);
        ChairPage chairPage = indexPage.selectAnyProduct();
        CartConfirmationPage cartConfirmationPage = chairPage.clickBuyButton(wait);
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "customers/1/cart/confirm"));

        String subtotalValue = cartConfirmationPage.getSubtotalBuyText();
        Assertions.assertEquals("Subtotal: R$ 10,00", subtotalValue);
        String freightValue = cartConfirmationPage.getFreightText();
        Assertions.assertEquals("Frete: R$ 8,90", freightValue);
        String totalValue = cartConfirmationPage.getTotalText();
        Assertions.assertEquals("Total: R$ 18,90", totalValue);
        OrderPaymentPage orderPaymentPage = cartConfirmationPage.clickConfirmButton();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "customers/1/orders/payment"));

        Double creditCardsTotalValue = orderPaymentPage.getValueInActiveCreditCards();
        Assertions.assertEquals(18.90, creditCardsTotalValue);

        ProfilePage profilePage = orderPaymentPage.confirmOrder();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "customers/1"));
        profilePage.scrollIntoSectionOfOrders(ProfilePage.OrdersStatus.PENDING);

        ProfilePage.Card card = profilePage.getCardFromSection(ProfilePage.OrdersStatus.PENDING, 1, 1);

        Assertions.assertEquals("Chair 1", card.getProductName());
        Assertions.assertEquals("R$ 10,00", card.getProductPrice());
        Assertions.assertEquals("1", card.getProductAmount());
        Assertions.assertEquals("Em processamento", card.getProductStatus());
        Assertions.assertEquals("R$ 10,00", card.getProductSubtotal());
        Assertions.assertEquals("R$ 8,90", card.getProductFreight());
        Assertions.assertEquals("R$ 18,90", card.getProductTotal());

        profilePage.accessHeaderOption(IndexPage.HeaderOptions.LOGOUT);
        usersInitializer.authWithAdmin(driver, wait);

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/customers"));
        driver.get(BASE_URL + "admin/orders");
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders"));
        AdminOrderIndexPage adminOrderIndexPage = new AdminOrderIndexPage(driver);
        OrderActions orderActions = adminOrderIndexPage.getMenuDivOfRow(1);

        PaymentStatusPage paymentStatusPage = (PaymentStatusPage) orderActions.clickAction("payment_status");
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders/1/payment"));
        Assertions.assertTrue(paymentStatusPage.isApproveButtonEnabled());
        Assertions.assertTrue(paymentStatusPage.isRejectButtonEnabled());
        ConfirmModal modal = paymentStatusPage.clickApprove();
        modal.confirm();

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders/1/payment"));

        Thread.sleep(1000);
        Assertions.assertFalse(paymentStatusPage.isRejectButtonEnabled());
        Assertions.assertFalse(paymentStatusPage.isApproveButtonEnabled());

        paymentStatusPage.clickBackButton();

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders"));
        OrderItemsPage orderItemsPage = (OrderItemsPage) adminOrderIndexPage.getMenuDivOfRow(1).clickAction("view_items");

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders/1"));

        Assertions.assertEquals(OrderStatus.APPROVED.getDescription(), orderItemsPage.getStatusOfRow(1));

        modal = orderItemsPage.dispatchDeliveryOfRow(1);
        modal.confirm();

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders/1"));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("table")));
        Assertions.assertEquals(OrderStatus.DELIVERING.getDescription(), orderItemsPage.getStatusOfRow(1));

        modal = orderItemsPage.confirmDeliveryOfRow(1);
        modal.confirm();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders/1"));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("table")));
        Thread.sleep(1000);
        Assertions.assertEquals(OrderStatus.DELIVERED.getDescription(), orderItemsPage.getStatusOfRow(1));

        orderItemsPage.clickBackButton();

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders"));
        adminOrderIndexPage.clickLogoutButton();

        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        usersInitializer.authWithCustomer(driver, wait);
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.PROFILE);
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "customers/1"));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("section.APPROVED")));
        profilePage.scrollIntoSectionOfOrders(ProfilePage.OrdersStatus.APPROVED);
        ProfilePage.SwapModal swapModal = profilePage.requestSwapOfCard(ProfilePage.OrdersStatus.APPROVED, 1);
        String swapAmount = swapModal.getSwapAmount();

        Assertions.assertEquals("1", swapAmount);
        swapModal.clickSwapButton();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "customers/1"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("section.APPROVED")));
        Thread.sleep(1000);
        profilePage.scrollIntoSectionOfOrders(ProfilePage.OrdersStatus.APPROVED);

        ProfilePage.Card swapCard = profilePage.getCardFromSection(ProfilePage.OrdersStatus.APPROVED, 1, 1);
        Assertions.assertTrue(swapCard.getProductStatus().equalsIgnoreCase(OrderStatus.SWAP_REQUEST.getDescription()));

        profilePage.accessHeaderOption(IndexPage.HeaderOptions.LOGOUT);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));

        usersInitializer.authWithAdmin(driver, wait);

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/customers"));

        driver.get(BASE_URL + "admin/orders");
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders"));
        adminOrderIndexPage = new AdminOrderIndexPage(driver);

        orderActions = adminOrderIndexPage.getMenuDivOfRow(1);
        orderActions.clickAction("view_items");
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders/1"));
        String status = orderItemsPage.getStatusOfRow(1);
        Assertions.assertEquals(OrderStatus.SWAP_REQUEST.getDescription(), status);

        SwapPage swapPage = orderItemsPage.redirectToOrdersSwaps();
        wait.until(ExpectedConditions.urlToBe(BASE_URL +"admin/orders/1/swaps"));
        modal = swapPage.approveSwapOfRow(1);
        modal.confirm();

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders/1/swaps"));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("table")));
        String statusAfterApprove = swapPage.getStatusOfRow(1);
        Assertions.assertEquals(OrderStatus.IN_SWAP.getDescription(), statusAfterApprove);

        SwapPage.ConfirmSwapModal confirmSwapModal = swapPage.confirmSwapOfRow(1);
        confirmSwapModal.clickReturnToStockCheckbox();

        confirmSwapModal.clickConfirmButton();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders/1/swaps"));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("table")));
        Thread.sleep(1000);
        String statusAfterConfirm = swapPage.getStatusOfRow(1);

        Assertions.assertEquals(OrderStatus.SWAPPED.getDescription(), statusAfterConfirm);
        swapPage.clickBackButton();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders/1"));
        orderItemsPage.clickBackButton();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders"));
        adminOrderIndexPage.clickLogoutButton();
        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        usersInitializer.authWithCustomer(driver, wait);

        indexPage.accessHeaderOption(IndexPage.HeaderOptions.PROFILE);
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "customers/1"));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("section.APPROVED")));

        profilePage.scrollIntoSectionOfOrders(ProfilePage.OrdersStatus.APPROVED);
        String amountSwapped = profilePage.getAmountToBeSwapped(1, 1);
        Assertions.assertEquals("1", amountSwapped);
    }

    @Test
    public void shouldNotSwapAChair() throws InterruptedException {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));

        IndexPage indexPage = new IndexPage(driver);
        ChairPage chairPage = indexPage.selectAnyProduct();
        CartConfirmationPage cartConfirmationPage = chairPage.clickBuyButton(wait);
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "customers/1/cart/confirm"));

        String subtotalValue = cartConfirmationPage.getSubtotalBuyText();
        Assertions.assertEquals("Subtotal: R$ 10,00", subtotalValue);
        String freightValue = cartConfirmationPage.getFreightText();
        Assertions.assertEquals("Frete: R$ 8,90", freightValue);
        String totalValue = cartConfirmationPage.getTotalText();
        Assertions.assertEquals("Total: R$ 18,90", totalValue);
        OrderPaymentPage orderPaymentPage = cartConfirmationPage.clickConfirmButton();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "customers/1/orders/payment"));

        Double creditCardsTotalValue = orderPaymentPage.getValueInActiveCreditCards();
        Assertions.assertEquals(18.90, creditCardsTotalValue);

        ProfilePage profilePage = orderPaymentPage.confirmOrder();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "customers/1"));
        profilePage.scrollIntoSectionOfOrders(ProfilePage.OrdersStatus.PENDING);

        ProfilePage.Card card = profilePage.getCardFromSection(ProfilePage.OrdersStatus.PENDING, 1, 1);

        Assertions.assertEquals("Chair 1", card.getProductName());
        Assertions.assertEquals("R$ 10,00", card.getProductPrice());
        Assertions.assertEquals("1", card.getProductAmount());
        Assertions.assertEquals("Em processamento", card.getProductStatus());
        Assertions.assertEquals("R$ 10,00", card.getProductSubtotal());
        Assertions.assertEquals("R$ 8,90", card.getProductFreight());
        Assertions.assertEquals("R$ 18,90", card.getProductTotal());

        profilePage.accessHeaderOption(IndexPage.HeaderOptions.LOGOUT);
        usersInitializer.authWithAdmin(driver, wait);

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/customers"));
        driver.get(BASE_URL + "admin/orders");
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders"));
        AdminOrderIndexPage adminOrderIndexPage = new AdminOrderIndexPage(driver);
        OrderActions orderActions = adminOrderIndexPage.getMenuDivOfRow(1);

        PaymentStatusPage paymentStatusPage = (PaymentStatusPage) orderActions.clickAction("payment_status");
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders/1/payment"));
        Assertions.assertTrue(paymentStatusPage.isApproveButtonEnabled());
        Assertions.assertTrue(paymentStatusPage.isRejectButtonEnabled());
        ConfirmModal modal = paymentStatusPage.clickApprove();
        modal.confirm();

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders/1/payment"));
        Thread.sleep(1000);
        Assertions.assertFalse(paymentStatusPage.isRejectButtonEnabled());
        Assertions.assertFalse(paymentStatusPage.isApproveButtonEnabled());

        paymentStatusPage.clickBackButton();

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders"));
        OrderItemsPage orderItemsPage = (OrderItemsPage) adminOrderIndexPage.getMenuDivOfRow(1).clickAction("view_items");

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders/1"));

        Assertions.assertEquals(OrderStatus.APPROVED.getDescription(), orderItemsPage.getStatusOfRow(1));

        modal = orderItemsPage.dispatchDeliveryOfRow(1);
        modal.confirm();

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders/1"));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("table")));
        Assertions.assertEquals(OrderStatus.DELIVERING.getDescription(), orderItemsPage.getStatusOfRow(1));

        modal = orderItemsPage.confirmDeliveryOfRow(1);
        modal.confirm();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders/1"));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("table")));
        Thread.sleep(1000);
        Assertions.assertEquals(OrderStatus.DELIVERED.getDescription(), orderItemsPage.getStatusOfRow(1));

        orderItemsPage.clickBackButton();

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders"));
        adminOrderIndexPage.clickLogoutButton();

        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        usersInitializer.authWithCustomer(driver, wait);
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.PROFILE);
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "customers/1"));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("section.APPROVED")));
        profilePage.scrollIntoSectionOfOrders(ProfilePage.OrdersStatus.APPROVED);
        ProfilePage.SwapModal swapModal = profilePage.requestSwapOfCard(ProfilePage.OrdersStatus.APPROVED, 1);
        String swapAmount = swapModal.getSwapAmount();

        Assertions.assertEquals("1", swapAmount);
        swapModal.clickSwapButton();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "customers/1"));
        Thread.sleep(2000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("section.APPROVED")));
        profilePage.scrollIntoSectionOfOrders(ProfilePage.OrdersStatus.APPROVED);

        ProfilePage.Card swapCard = profilePage.getCardFromSection(ProfilePage.OrdersStatus.APPROVED, 1, 1);
        Assertions.assertTrue(swapCard.getProductStatus().equalsIgnoreCase(OrderStatus.SWAP_REQUEST.getDescription()));

        profilePage.accessHeaderOption(IndexPage.HeaderOptions.LOGOUT);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));

        usersInitializer.authWithAdmin(driver, wait);

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/customers"));

        driver.get(BASE_URL + "admin/orders");
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders"));
        adminOrderIndexPage = new AdminOrderIndexPage(driver);

        orderActions = adminOrderIndexPage.getMenuDivOfRow(1);
        orderActions.clickAction("view_items");
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders/1"));
        String status = orderItemsPage.getStatusOfRow(1);
        Assertions.assertEquals(OrderStatus.SWAP_REQUEST.getDescription(), status);

        SwapPage swapPage = orderItemsPage.redirectToOrdersSwaps();
        wait.until(ExpectedConditions.urlToBe(BASE_URL +"admin/orders/1/swaps"));
        modal = swapPage.reproveSwapOfRow(1);
        modal.confirm();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders/1/swaps"));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("table")));
        Thread.sleep(1000);
        String statusAfterApprove = swapPage.getStatusOfRow(1);
        Assertions.assertEquals(OrderStatus.SWAP_REPROVED.getDescription(), statusAfterApprove);
        swapPage.clickBackButton();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders/1"));
        orderItemsPage.clickBackButton();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/orders"));
        adminOrderIndexPage.clickLogoutButton();
        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        usersInitializer.authWithCustomer(driver, wait);
        indexPage.accessHeaderOption(IndexPage.HeaderOptions.PROFILE);
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "customers/1"));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("section.APPROVED")));
        profilePage.scrollIntoSectionOfOrders(ProfilePage.OrdersStatus.APPROVED);
        card = profilePage.getCardFromSection(ProfilePage.OrdersStatus.APPROVED, 1, 1);
        Assertions.assertEquals("Troca reprovada", card.getProductStatus());

    }

    @AfterEach
    public void tearDown() {
        driver.quit();
        seeder.truncateAllTables();
    }

}

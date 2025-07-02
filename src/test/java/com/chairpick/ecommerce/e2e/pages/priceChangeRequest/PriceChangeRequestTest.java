package com.chairpick.ecommerce.e2e.pages.priceChangeRequest;

import com.chairpick.ecommerce.e2e.factories.WebDriverFactory;
import com.chairpick.ecommerce.e2e.pageObjects.chairs.AdminChairs;
import com.chairpick.ecommerce.e2e.pageObjects.chairs.UpdateChairPage;
import com.chairpick.ecommerce.e2e.pageObjects.components.ConfirmModal;
import com.chairpick.ecommerce.e2e.pageObjects.priceChangeRequest.PriceChangeRequestsPage;
import com.chairpick.ecommerce.e2e.utils.ChairInitializer;
import com.chairpick.ecommerce.e2e.utils.ContainerInitializer;
import com.chairpick.ecommerce.e2e.utils.DatabaseSeeder;
import com.chairpick.ecommerce.e2e.utils.UsersInitializer;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PriceChangeRequestTest {

    @Autowired
    private DatabaseSeeder seeder;
    @MockitoBean
    private OllamaEmbeddingModel mockModel;
    private final String BASE_URL = "http://localhost:8080/";
    private WebDriver driver;
    private WebDriverWait wait;
    private UsersInitializer usersInitializer;


    private void configureEmbeddingMock() {
        List<Float> embedding = IntStream.range(0, 768)
                .mapToDouble(i -> Math.random()).boxed()
                .map(Double::floatValue)
                .toList();
        float[] embeddingArray = new float[embedding.size()];
        for (int i = 0; i < embedding.size(); i++) {
            embeddingArray[i] = embedding.get(i);
        }
        Mockito.when(mockModel.embed(Mockito.anyString())).thenReturn(embeddingArray);
    }

    @BeforeEach
    public void setUp() {
        configureEmbeddingMock();
        driver = WebDriverFactory.createWebDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.manage().window().maximize();

        usersInitializer = new UsersInitializer(seeder);
        ChairInitializer chairInitializer = new ChairInitializer(seeder);
        usersInitializer.createDefaultAdminAndSalesManager().authWithAdmin(driver, wait);
        chairInitializer.seedDefaultProducts();
    }

    @Test
    public void shouldCreateAnApprovedPriceChangeRequest() throws InterruptedException {

        driver.get(BASE_URL + "admin/chairs");
        AdminChairs adminChairs = new AdminChairs(driver);

        AdminChairs.Actions actions = adminChairs.openActionsOfRow(1);
        UpdateChairPage updateChairPage = actions.openUpdateChair();
        wait.until(ExpectedConditions.urlContains("/edit"));
        updateChairPage.selectToAlterPrice();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("alter_price_section")));
        String price = updateChairPage.keyAlterPrice("1000");
        Assertions.assertEquals("1000", price, "Price input should be 1000");
        String reason = updateChairPage.keyAlterPriceReason("Test reason");
        Assertions.assertEquals("Test reason", reason, "Reason input should be 'Test reason'");
        Assertions.assertTrue(updateChairPage.isSubmitButtonEnabled(), "Submit button should be enabled");
        AdminChairs updatedChairs = updateChairPage.submit();
        wait.until(ExpectedConditions.urlContains("/admin/chairs"));
        Thread.sleep(2000);
        price = updatedChairs.getActualPrice(1);
        Assertions.assertEquals("R$ 10,00", price, "Price of the chair should be 10,00, not updated yet");
        actions = updatedChairs.openActionsOfRow(1);
        PriceChangeRequestsPage priceChangeRequestsPage = actions.openPriceChangeRequests();
        wait.until(ExpectedConditions.urlContains("/price-change-requests"));
        Thread.sleep(2000);
        String requestedPrice = priceChangeRequestsPage.getRequestedPriceOfRow(1);
        Assertions.assertEquals("R$ 1000,00", requestedPrice, "Requested price should be R$ 1.000,00");
        String reasonOfRow = priceChangeRequestsPage.getReasonOfRow(1);
        Assertions.assertEquals("Test reason", reasonOfRow, "Reason of the price change request should be 'Test reason'");
        String status = priceChangeRequestsPage.getStatusOfRow(1);
        Assertions.assertEquals("Pendente", status, "Status of the price change request should be 'Pendente'");
        Assertions.assertEquals(0, priceChangeRequestsPage.getActionsElements(1).size(), "There should be no actions available for admin, only sales manager can see");

        // Sales manager approves the request
        adminChairs = priceChangeRequestsPage.goBackToAdminChairs();
        adminChairs.logout();
        usersInitializer.authWithSalesManager(driver, wait);

        driver.get(BASE_URL + "admin/chairs");
        adminChairs = new AdminChairs(driver);
        actions = adminChairs.openActionsOfRow(1);
        priceChangeRequestsPage = actions.openPriceChangeRequestAsSalesManager();
        wait.until(ExpectedConditions.urlContains("/price-change-requests"));
        Thread.sleep(2000);
        Assertions.assertEquals(2, priceChangeRequestsPage.getActionsElements(1).size(), "There should be 2 actions available for sales manager");
        ConfirmModal modal = priceChangeRequestsPage.approvePriceChangeRequest(1);
        Thread.sleep(2000);
        modal.confirm();
        wait.until(ExpectedConditions.urlContains("/admin/chairs"));
        Thread.sleep(2000);
        status = priceChangeRequestsPage.getStatusOfRow(1);
        Assertions.assertEquals("Autorizado", status, "Status of the price change request should be 'Aprovado'");
        adminChairs= priceChangeRequestsPage.goBackToAdminChairs();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/chairs"));
        price = adminChairs.getActualPrice(1);
        Assertions.assertEquals("R$ 1000,00", price, "Price of the chair should be updated to R$ 1.000,00");

    }

    @Test
    public void shouldCreateAnReprovedPriceChangeRequest() throws InterruptedException {
        driver.get(BASE_URL + "admin/chairs");
        AdminChairs adminChairs = new AdminChairs(driver);

        AdminChairs.Actions actions = adminChairs.openActionsOfRow(1);
        UpdateChairPage updateChairPage = actions.openUpdateChair();
        wait.until(ExpectedConditions.urlContains("/edit"));
        updateChairPage.selectToAlterPrice();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("alter_price_section")));
        String price = updateChairPage.keyAlterPrice("1000");
        Assertions.assertEquals("1000", price, "Price input should be 1000");
        String reason = updateChairPage.keyAlterPriceReason("Test reason");
        Assertions.assertEquals("Test reason", reason, "Reason input should be 'Test reason'");
        Assertions.assertTrue(updateChairPage.isSubmitButtonEnabled(), "Submit button should be enabled");
        AdminChairs updatedChairs = updateChairPage.submit();
        wait.until(ExpectedConditions.urlContains("/admin/chairs"));
        Thread.sleep(2000);
        price = updatedChairs.getActualPrice(1);
        Assertions.assertEquals("R$ 10,00", price, "Price of the chair should be 10,00, not updated yet");
        actions = updatedChairs.openActionsOfRow(1);
        PriceChangeRequestsPage priceChangeRequestsPage = actions.openPriceChangeRequests();
        wait.until(ExpectedConditions.urlContains("/price-change-requests"));
        Thread.sleep(2000);
        String requestedPrice = priceChangeRequestsPage.getRequestedPriceOfRow(1);
        Assertions.assertEquals("R$ 1000,00", requestedPrice, "Requested price should be R$ 1.000,00");
        String reasonOfRow = priceChangeRequestsPage.getReasonOfRow(1);
        Assertions.assertEquals("Test reason", reasonOfRow, "Reason of the price change request should be 'Test reason'");
        String status = priceChangeRequestsPage.getStatusOfRow(1);
        Assertions.assertEquals("Pendente", status, "Status of the price change request should be 'Pendente'");


        adminChairs = priceChangeRequestsPage.goBackToAdminChairs();
        adminChairs.logout();
        usersInitializer.authWithSalesManager(driver, wait);

        driver.get(BASE_URL + "admin/chairs");
        adminChairs = new AdminChairs(driver);
        actions = adminChairs.openActionsOfRow(1);
        priceChangeRequestsPage = actions.openPriceChangeRequestAsSalesManager();
        wait.until(ExpectedConditions.urlContains("/price-change-requests"));
        Thread.sleep(2000);
        Assertions.assertEquals(2, priceChangeRequestsPage.getActionsElements(1).size(), "There should be 2 actions available for sales manager");
        ConfirmModal modal = priceChangeRequestsPage.rejectPriceChangeRequest(1);
        Thread.sleep(2000);
        modal.confirm();
        Thread.sleep(2000);
        wait.until(ExpectedConditions.urlContains("/admin/chairs"));
        status = priceChangeRequestsPage.getStatusOfRow(1);
        Assertions.assertEquals("Reprovado", status, "Status of the price change request should be 'Reprovado'");
        adminChairs = priceChangeRequestsPage.goBackToAdminChairs();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/chairs"));
        price = adminChairs.getActualPrice(1);
        Assertions.assertEquals("R$ 10,00", price, "Price of the chair should remain R$ 10,00, not updated");

    }

    @AfterEach
    public void setDown() {
        driver.quit();
        seeder.truncateAllTables();
    }


}

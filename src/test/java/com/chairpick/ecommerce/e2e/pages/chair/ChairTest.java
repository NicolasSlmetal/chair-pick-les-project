package com.chairpick.ecommerce.e2e.pages.chair;

import com.chairpick.ecommerce.e2e.factories.WebDriverFactory;
import com.chairpick.ecommerce.e2e.pageObjects.chairs.AdminChairs;
import com.chairpick.ecommerce.e2e.pageObjects.chairs.ChairSearchPage;
import com.chairpick.ecommerce.e2e.pageObjects.chairs.CreateChairPage;
import com.chairpick.ecommerce.e2e.pageObjects.chairs.UpdateChairPage;
import com.chairpick.ecommerce.e2e.pageObjects.index.IndexPage;
import com.chairpick.ecommerce.e2e.pageObjects.item.AddToStockPage;
import com.chairpick.ecommerce.e2e.utils.ChairInitializer;
import com.chairpick.ecommerce.e2e.utils.ContainerInitializer;
import com.chairpick.ecommerce.e2e.utils.DatabaseSeeder;
import com.chairpick.ecommerce.e2e.utils.UsersInitializer;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ChairTest {

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
        usersInitializer.createDefaultAdminAndCustomer().authWithCustomer(driver, wait);
        chairInitializer.seedDefaultProducts();
    }

    @Test
    public void shouldSearchForChair() throws InterruptedException {
        driver.get(BASE_URL);
        IndexPage indexPage = new IndexPage(driver);

        String text = indexPage.writeTextToSearch("Chair 1");

        Assertions.assertEquals("Chair 1", text, "Search text should match the input");
        ChairSearchPage chairSearchPage = indexPage.clickSearchButton();

        wait.until(ExpectedConditions.urlContains("/search?name=Chair+1"));

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".product_card")));
        List<WebElement> searchedChairs = chairSearchPage.getAllSearchedChairs();
        Assertions.assertEquals(1, searchedChairs.size());

        chairSearchPage.accessHeaderOption(IndexPage.HeaderOptions.HOME);

        String minPrice = indexPage.writeMinPriceToSearch("100");
        Assertions.assertEquals("100", minPrice, "Min price should match the input");
        String maxPrice = indexPage.writeMaxPriceToSearch("200");
        Assertions.assertEquals("200", maxPrice, "Max price should match the input");

        chairSearchPage = indexPage.clickSearchButton();
        searchedChairs = chairSearchPage.getAllSearchedChairs();
        Assertions.assertEquals(0, searchedChairs.size(), "Should find no chair in the price range");

        chairSearchPage.accessHeaderOption(IndexPage.HeaderOptions.HOME);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input.select2-search__field")));
        Thread.sleep(2000);
        String category = indexPage.writeCategoryToSearch("Category");
        Assertions.assertEquals("Category", category, "Category should match the input");
        chairSearchPage = indexPage.clickSearchButton();
        Thread.sleep(2000);
        searchedChairs = chairSearchPage.getAllSearchedChairs();
        Assertions.assertEquals(1, searchedChairs.size(), "Should find one chair in the category");

        chairSearchPage.accessHeaderOption(IndexPage.HeaderOptions.HOME);

        String minLength = indexPage.writeMinLengthToSearch("100");
        Assertions.assertEquals("100", minLength, "Min length should match the input");
        String maxLength = indexPage.writeMaxLengthToSearch("200");
        Assertions.assertEquals("200", maxLength, "Max length should match the input");
        chairSearchPage = indexPage.clickSearchButton();
        wait.until(ExpectedConditions.urlContains("/search?min_length=100&max_length=200"));
        searchedChairs = chairSearchPage.getAllSearchedChairs();
        Assertions.assertEquals(0, searchedChairs.size(), "Should find no chair in the length range");

        chairSearchPage.accessHeaderOption(IndexPage.HeaderOptions.HOME);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        String minWidth = indexPage.writeMinWidthToSearch("100");
        Assertions.assertEquals("100", minWidth, "Min width should match the input");
        String maxWidth = indexPage.writeMaxWidthToSearch("200");
        Assertions.assertEquals("200", maxWidth, "Max width should match the input");
        chairSearchPage = indexPage.clickSearchButton();
        wait.until(ExpectedConditions.urlContains("/search?min_width=100&max_width=200"));
        searchedChairs = chairSearchPage.getAllSearchedChairs();
        Assertions.assertEquals(0, searchedChairs.size(), "Should find no chair in the width range");
        chairSearchPage.accessHeaderOption(IndexPage.HeaderOptions.HOME);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        String minHeight = indexPage.writeMinHeightToSearch("10");
        Assertions.assertEquals("10", minHeight, "Min height should match the input");
        String maxHeight = indexPage.writeMaxHeightToSearch("200");
        Assertions.assertEquals("200", maxHeight, "Max height should match the input");

        chairSearchPage = indexPage.clickSearchButton();
        wait.until(ExpectedConditions.urlContains("/search?min_height=10&max_height=200"));
        searchedChairs = chairSearchPage.getAllSearchedChairs();
        Assertions.assertEquals(1, searchedChairs.size(), "Should find one chair in the weight range");

        chairSearchPage.accessHeaderOption(IndexPage.HeaderOptions.HOME);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));

        String minRating = indexPage.writeMinAverageRatingToSearch("4");
        Assertions.assertEquals("4", minRating, "Min rating should match the input");
        String maxRating = indexPage.writeMaxAverageRatingToSearch("5");
        Assertions.assertEquals("5", maxRating, "Max rating should match the input");
        chairSearchPage = indexPage.clickSearchButton();
        wait.until(ExpectedConditions.urlContains("/search?min_rating=4&max_rating=5"));
        searchedChairs = chairSearchPage.getAllSearchedChairs();
        Assertions.assertEquals(1, searchedChairs.size(), "Should find one chair in the rating range");


        chairSearchPage.accessHeaderOption(IndexPage.HeaderOptions.HOME);
        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        String name = indexPage.writeTextToSearch("Chair 1");
        Assertions.assertEquals("Chair 1", name, "Search text should match the input");
        String combinedCategory = indexPage.writeCategoryToSearch("Category");
        Assertions.assertEquals("Category", combinedCategory, "Category should match the input");
        String combinedMinPrice = indexPage.writeMinPriceToSearch("5");
        Assertions.assertEquals("5", combinedMinPrice, "Min price should match the input");
        String combinedMaxPrice = indexPage.writeMaxPriceToSearch("1000");
        Assertions.assertEquals("1000", combinedMaxPrice, "Max price should match the input");
        String combinedMinLength = indexPage.writeMinLengthToSearch("10");
        Assertions.assertEquals("10", combinedMinLength, "Min length should match the input");
        String combinedMaxLength = indexPage.writeMaxLengthToSearch("200");
        Assertions.assertEquals("200", combinedMaxLength, "Max length should match the input");
        String combinedMinWidth = indexPage.writeMinWidthToSearch("10");
        Assertions.assertEquals("10", combinedMinWidth, "Min width should match the input");
        String combinedMaxWidth = indexPage.writeMaxWidthToSearch("200");
        Assertions.assertEquals("200", combinedMaxWidth, "Max width should match the input");
        String combinedMinHeight = indexPage.writeMinHeightToSearch("10");
        Assertions.assertEquals("10", combinedMinHeight, "Min height should match the input");
        String combinedMaxHeight = indexPage.writeMaxHeightToSearch("200");
        Assertions.assertEquals("200", combinedMaxHeight, "Max height should match the input");
        String combinedMinRating = indexPage.writeMinAverageRatingToSearch("4");
        Assertions.assertEquals("4", combinedMinRating, "Min rating should match the input");
        String combinedMaxRating = indexPage.writeMaxAverageRatingToSearch("5");
        Assertions.assertEquals("5", combinedMaxRating, "Max rating should match the input");
        chairSearchPage = indexPage.clickSearchButton();
        wait.until(ExpectedConditions.urlContains("/search"));
        wait.until(ExpectedConditions.urlContains("name=Chair"));
        wait.until(ExpectedConditions.urlContains("categories=Category"));
        wait.until(ExpectedConditions.urlContains("min_price=5"));
        wait.until(ExpectedConditions.urlContains("max_price=1000"));
        wait.until(ExpectedConditions.urlContains("min_length=10"));
        wait.until(ExpectedConditions.urlContains("max_length=200"));
        wait.until(ExpectedConditions.urlContains("min_width=10"));
        wait.until(ExpectedConditions.urlContains("max_width=200"));
        wait.until(ExpectedConditions.urlContains("min_height=10"));
        wait.until(ExpectedConditions.urlContains("max_height=200"));
        wait.until(ExpectedConditions.urlContains("min_rating=4"));
        wait.until(ExpectedConditions.urlContains("max_rating=5"));
        searchedChairs = chairSearchPage.getAllSearchedChairs();
        Assertions.assertEquals(1, searchedChairs.size(), "Should find one chair with all filters applied");
    }

    @Test
    public void shouldCreateAChair() throws InterruptedException {
        driver.get(BASE_URL);
        usersInitializer.authWithAdmin(driver, wait);
        driver.get(BASE_URL + "admin/chairs");
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/chairs"));
        AdminChairs adminChairs = new AdminChairs(driver);
        CreateChairPage createChairPage = adminChairs.selectToCreateAChair();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/chairs/new"));
        Thread.sleep(2000);
        String chairName = createChairPage.keyName("New Chair");
        Assertions.assertEquals("New Chair", chairName, "Chair name should match the input");
        String chairDescription = createChairPage.keyDescription("This is a new chair");
        Assertions.assertEquals("This is a new chair", chairDescription, "Chair description should match the input");
        String category = createChairPage.keyCategory("Category");
        Assertions.assertEquals("1", category, "Chair category should match the input");
        String length = createChairPage.keyLength("100");
        Assertions.assertEquals("100", length, "Chair length should match the input");
        String width = createChairPage.keyWidth("50");
        Assertions.assertEquals("50", width, "Chair width should match the input");
        String height = createChairPage.keyHeight("150");
        Assertions.assertEquals("150", height, "Chair height should match the input");
        String weight = createChairPage.keyWeight("10");
        Assertions.assertEquals("10", weight, "Chair weight should match the input");
        String pricingGroup = createChairPage.keyPricingGroup("1");
        Assertions.assertEquals("1", pricingGroup, "Chair pricing group should match the input");
        String rating = createChairPage.keyRating("4.5");
        Assertions.assertEquals("4.5", rating, "Chair rating should match the input");
        Path dir = Path.of(System.getProperty("user.dir"));
        Path path = Path.of("images-test/default.png");
        Path resolvedPath = dir.resolve(path);

        String insertedFile = createChairPage.keyImage(resolvedPath);
        Assertions.assertTrue(insertedFile.contains("default.png"), "Chair image should be set correctly");

        Assertions.assertTrue(createChairPage.isSubmitButtonEnabled());
        AdminChairs adminChairsPage = createChairPage.submitChair();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/chairs"));

        String chairNameFromTable = adminChairsPage.getChairNameFromRow(1);
        Assertions.assertEquals("New Chair", chairNameFromTable, "Chair name should be visible in the admin chairs table");

        String actualPrice = adminChairsPage.getActualPrice(1);
        Assertions.assertEquals("R$ 0,00", actualPrice, "Chair actual price should be 0.00 because there is no stock yet");
        String dimensions = adminChairsPage.getDimensionsOfRow(1);
        Assertions.assertEquals("100.0 cm x 50.0 cm x 150.0 cm", dimensions, "Chair dimensions should match the input");
        String weightOfChair = adminChairsPage.getWeightOfRow(1);
        Assertions.assertEquals("10.0 kg", weightOfChair, "Chair weight should match the input");
        String higherCost = adminChairsPage.getHigherCostOfRow(1);
        Assertions.assertEquals("R$ 0,00", higherCost, "Chair higher cost should be 0.00 because there is no stock yet");
        String resultPricingGroup = adminChairsPage.getPricingGroupOfRow(1);
        Assertions.assertEquals("Any", resultPricingGroup, "Chair pricing group should match the input");
        String averageRating = adminChairsPage.getAverageRatingOfRow(1);
        String stockAmount = adminChairsPage.getStockOfRow(1);
        Assertions.assertEquals("0", stockAmount, "Chair stock amount should be 0 because there is no stock yet");
        String status = adminChairsPage.getStatusOfRow(1);
        Assertions.assertEquals("Ativo", status, "Chair status should be Active");
        Assertions.assertEquals("4.5", averageRating, "Chair average rating should match the input");
        String lastEntryDate = adminChairsPage.getLastEntryStockOfRow(1);
        Assertions.assertEquals("Nenhuma entrada", lastEntryDate, "Chair should not have any stock yet");

        AdminChairs.Actions actions = adminChairsPage.openActionsOfRow(1);
        Assertions.assertNotNull(actions);
        AddToStockPage addToStockPage = actions.openAddStock();
        Assertions.assertNotNull(addToStockPage);

        wait.until(ExpectedConditions.urlContains("items/new"));
        String amount = addToStockPage.keyAmount("10");
        Assertions.assertEquals("10", amount, "Amount should match the input");
        String cost = addToStockPage.keyCost("100");
        Assertions.assertEquals("100", cost, "Cost should match the input");
        String entryDate = addToStockPage.keyEntryDate("2023-10-01");
        Assertions.assertEquals("2023-10-01", entryDate, "Entry date should match the input");
        String supplier = addToStockPage.keySupplier("Supplier 1");
        Assertions.assertEquals("Supplier 1", supplier, "Supplier should match the input");
        Assertions.assertTrue(addToStockPage.isSubmitButtonEnabled(), "Submit button should be enabled");


        adminChairs = addToStockPage.submit();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/chairs"));
        stockAmount = adminChairs.getStockOfRow(1);
        Assertions.assertEquals("10", stockAmount, "Chair stock amount should be 10 after adding stock");
        actualPrice = adminChairs.getActualPrice(1);
        Assertions.assertEquals("R$ 120,00", actualPrice, "Chair actual price should be 110.00 after adding stock");
        higherCost = adminChairs.getHigherCostOfRow(1);
        Assertions.assertEquals("R$ 100,00", higherCost, "Chair higher cost should be 100.00 after adding stock");

        IndexPage indexPage = adminChairsPage.logout();
        wait.until(ExpectedConditions.urlToBe(BASE_URL));

        Assertions.assertEquals(4, indexPage.getAllProducts().size(), "Should have 2 products in the index page after adding stock");
        Thread.sleep(2000);
    }

    @Test
    public void shouldUpdateAChair() throws InterruptedException {

        driver.get(BASE_URL);
        usersInitializer.authWithAdmin(driver, wait);
        driver.get(BASE_URL + "admin/chairs");
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/chairs"));
        AdminChairs adminChairs = new AdminChairs(driver);

        AdminChairs.Actions actions = adminChairs.openActionsOfRow(1);
        Assertions.assertNotNull(actions, "Actions should not be null");
        UpdateChairPage createChairPage = actions.openUpdateChair();

        Assertions.assertNotNull(createChairPage, "Update chair page should not be null");
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/chairs/1/edit"));

        String chairName = createChairPage.keyChairName("Updated Chair");
        Assertions.assertEquals("Updated Chair", chairName, "Chair name should match the input");
        Assertions.assertTrue(createChairPage.isSubmitButtonEnabled(), "Submit button should be enabled");
        adminChairs = createChairPage.submit();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/chairs"));
        String chairNameFromTable = adminChairs.getChairNameFromRow(1);
        Assertions.assertEquals("Updated Chair", chairNameFromTable, "Chair name should be updated in the admin chairs table");

    }

    @Test
    public void shouldManageChairStatus() throws InterruptedException {
        driver.get(BASE_URL);
        usersInitializer.authWithAdmin(driver, wait);
        driver.get(BASE_URL + "admin/chairs");
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/chairs"));
        AdminChairs adminChairs = new AdminChairs(driver);

        AdminChairs.Actions actions = adminChairs.openActionsOfRow(1);
        Assertions.assertNotNull(actions, "Actions should not be null");
        AdminChairs.ChairStatusChangeModal changeStatusModal = actions.openChangeStatus();
        Assertions.assertNotNull(changeStatusModal, "Change status modal should not be null");
        String reason = changeStatusModal.keyReason("Chair is outdated");
        Assertions.assertEquals("Chair is outdated", reason, "Reason should match the input");
        changeStatusModal.confirm();
        Thread.sleep(2000);
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/chairs"));
        String status = adminChairs.getStatusOfRow(1);
        Assertions.assertEquals("Inativo", status, "Chair status should be Inactive after changing status");
        IndexPage indexPage = adminChairs.logout();
        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        Assertions.assertEquals(0, indexPage.getAllProducts().size(), "Should have 0 products in the index page after changing status to Inactive");
        usersInitializer.authWithAdmin(driver, wait);
        driver.get(BASE_URL + "admin/chairs");
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/chairs"));
        adminChairs = new AdminChairs(driver);
        actions = adminChairs.openActionsOfRow(1);
        Assertions.assertNotNull(actions, "Actions should not be null");
        changeStatusModal = actions.openChangeStatus();
        Assertions.assertNotNull(changeStatusModal, "Change status modal should not be null");
        changeStatusModal.keyReason("Chair is back in stock");
        changeStatusModal.confirm();
        Thread.sleep(2000);
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "admin/chairs"));
        status = adminChairs.getStatusOfRow(1);
        Assertions.assertEquals("Ativo", status, "Chair status should be Active after changing status back");
        indexPage = adminChairs.logout();
        wait.until(ExpectedConditions.urlToBe(BASE_URL));
        Thread.sleep(10000);
        Assertions.assertEquals(2, indexPage.getAllProducts().size(), "Should have 2 products in the index page after changing status back to Active");
    }

    @AfterEach
    public void setDown() {
        driver.quit();
        seeder.truncateAllTables();
        eliminateExtraFilesOfTestDir();
    }

    private void eliminateExtraFilesOfTestDir() {
        String baseDir = System.getProperty("user.dir");
        Path imagePath = Path.of(baseDir + "/images-test");
        String[] protectedFiles = {"default.png", "chair1.png", "chair2.png"};
        try {
            Files.list(imagePath)
                    .filter(path -> !path.getFileName().toString().startsWith("default.png") &&
                            !path.getFileName().toString().startsWith("chair1.png") &&
                            !path.getFileName().toString().startsWith("chair2.png"))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

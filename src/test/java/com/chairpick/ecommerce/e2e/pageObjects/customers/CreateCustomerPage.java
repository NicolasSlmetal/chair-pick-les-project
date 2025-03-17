package com.chairpick.ecommerce.e2e.pageObjects.customers;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Objects;

public class CreateCustomerPage extends PageObject {


    private static final String URL = "http://localhost:8080/customers/new";
    private final By addressSection = By.cssSelector("section#delivery_section");
    private final By creditCardSection = By.cssSelector("section#credit_card_section");
    private final By nameInput = By.cssSelector("input#name");
    private final By cpfInput = By.cssSelector("input#cpf");
    private final By emailInput = By.cssSelector("input#email");
    private final By phoneInput = By.cssSelector("input#phone");
    private final By phoneTypeSelect = By.cssSelector("select#phone_type");
    private final By genreSelect = By.cssSelector("select#genre");
    private final By bornDateInput = By.cssSelector("input#birthdate");
    private final By addressName = By.cssSelector("input#name_delivery");
    private final By addressCep = By.cssSelector("input#cep_delivery");
    private final By addressNumber = By.cssSelector("input#number_delivery");
    private final By addressNeighborhood = By.cssSelector("input#neighborhood_delivery");
    private final By addressCity = By.cssSelector("input#city_delivery");
    private final By addressState = By.cssSelector("input#state_delivery");
    private final By addressCountry = By.cssSelector("input#country_delivery");
    private final By addressStreet = By.cssSelector("input#address_delivery");
    private final By addressStreetType = By.cssSelector("select#address_delivery_type");
    private final By addressObservations = By.cssSelector("textarea#delivery_observations");
    private final By addressButtonSubmit = By.cssSelector("button#delivery");
    private final By creditCardNumber = By.cssSelector("input#card_number");
    private final By creditCardName = By.cssSelector("input#card_name");
    private final By creditCardCvv = By.cssSelector("input#cvv");
    private final By getCreditCardSubmitButton = By.cssSelector("button#credit_card");
    private final By submitButton = By.cssSelector("button[type='submit']");
    private final By cardsSelector = By.cssSelector(".card");
    private final By passwordInput = By.cssSelector("input#password");
    private final By passwordConfirmInput = By.cssSelector("input#password_confirmation");

    public CreateCustomerPage(WebDriver driver) {
        super(driver);
        verifyIfIsTheCorrectPage();
    }

    @Override
    public void verifyIfIsTheCorrectPage() {
        if (!Objects.requireNonNull(driver.getCurrentUrl()).contains(URL)) {
            throw new IllegalStateException("This is not the create customer page");
        }
    }

    public String fillName(String name) {
        driver.findElement(nameInput).sendKeys(name);
        return driver.findElement(nameInput).getDomProperty("value");
    }

    public String fillCpf(String cpf) {
        driver.findElement(cpfInput).sendKeys(cpf);
        return driver.findElement(cpfInput).getDomProperty("value");
    }

    public String fillEmail(String email) {
        driver.findElement(emailInput).sendKeys(email);
        return driver.findElement(emailInput).getDomProperty("value");
    }

    public String fillPhone(String phone) {
        driver.findElement(phoneInput).sendKeys(phone);
        return driver.findElement(phoneInput).getDomProperty("value");
    }

    public String fillPhoneType(String phoneType) {
        driver.findElement(phoneTypeSelect).sendKeys(phoneType);
        return driver.findElement(phoneTypeSelect).getDomProperty("value");
    }

    public String fillGenre(String genre) {
        driver.findElement(genreSelect).sendKeys(genre);
        return driver.findElement(genreSelect).getDomProperty("value");
    }

    public String fillBornDate(String bornDate) {
        ((JavascriptExecutor) driver).executeScript("document.getElementById('birthdate').value = '" + bornDate + "';" +
                "document.getElementById('birthdate').dispatchEvent(new Event('input', { bubbles: true }));" +
                "document.getElementById('birthdate').dispatchEvent(new Event('change', { bubbles: true }));");
        return driver.findElement(bornDateInput).getDomProperty("value");
    }

    public String fillAddressName(String addressName) {
        driver.findElement(this.addressName).sendKeys(addressName);
        return driver.findElement(this.addressName).getDomProperty("value");
    }

    public String fillAddressCep(String addressCep) {
        driver.findElement(this.addressCep).sendKeys(addressCep);
        return driver.findElement(this.addressCep).getDomProperty("value");
    }

    public String fillAddressNumber(String addressNumber) {
        driver.findElement(this.addressNumber).sendKeys(addressNumber);
        return driver.findElement(this.addressNumber).getDomProperty("value");
    }

    public String fillAddressStreet(String addressStreet) {
        driver.findElement(this.addressStreet).sendKeys(addressStreet);
        return driver.findElement(this.addressStreet).getDomProperty("value");
    }

    public String fillAddressNeighborhood(String addressNeighborhood) {
        driver.findElement(this.addressNeighborhood).sendKeys(addressNeighborhood);
        return driver.findElement(this.addressNeighborhood).getDomProperty("value");
    }

    public String fillAddressCity(String addressCity) {
        driver.findElement(this.addressCity).sendKeys(addressCity);
        return driver.findElement(this.addressCity).getDomProperty("value");
    }

    public String fillAddressState(String addressState) {
        driver.findElement(this.addressState).sendKeys(addressState);
        return driver.findElement(this.addressState).getDomProperty("value");
    }

    public String fillAddressCountry(String addressCountry) {
        driver.findElement(this.addressCountry).sendKeys(addressCountry);
        return driver.findElement(this.addressCountry).getDomProperty("value");
    }

    public String fillAddressStreetType(String addressStreetType) {
        driver.findElement(this.addressStreetType).sendKeys(addressStreetType);
        return driver.findElement(this.addressStreetType).getDomProperty("value");
    }

    public String fillAddressObservations(String addressObservations) {
        driver.findElement(this.addressObservations).sendKeys(addressObservations);
        return driver.findElement(this.addressObservations).getDomProperty("value");
    }

    public void submitAddress() {
        driver.findElement(this.addressButtonSubmit).click();
    }

    public String fillCreditCardNumber(String creditCardNumber) {
        driver.findElement(this.creditCardNumber).sendKeys(creditCardNumber);
        return driver.findElement(this.creditCardNumber).getDomProperty("value");
    }

    public String fillCreditCardName(String creditCardName) {
        driver.findElement(this.creditCardName).sendKeys(creditCardName);
        return driver.findElement(this.creditCardName).getDomProperty("value");
    }

    public String fillCreditCardCvv(String creditCardCvv) {
        driver.findElement(this.creditCardCvv).sendKeys(creditCardCvv);
        return driver.findElement(this.creditCardCvv).getDomProperty("value");
    }

    public String fillPassword(String password) {
        driver.findElement(passwordInput).sendKeys(password);
        return driver.findElement(passwordInput).getDomProperty("value");
    }

    public String fillPasswordConfirm(String password) {
        driver.findElement(passwordConfirmInput).sendKeys(password);
        return driver.findElement(passwordConfirmInput).getDomProperty("value");
    }

    public void submitCreditCard() {
        driver.findElement(this.getCreditCardSubmitButton).click();
    }

    public void submit() {
        driver.findElement(this.submitButton).click();
    }

    public List<WebElement> getCardsFromAddressSection() {
        return driver.findElement(addressSection).findElements(cardsSelector);
    }

    public List<WebElement> getCardsFromCreditCardSection() {
        return driver.findElement(creditCardSection).findElements(cardsSelector);
    }

    public static String getUrl() {
        return URL;
    }

}

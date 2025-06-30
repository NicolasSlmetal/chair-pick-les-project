package com.chairpick.ecommerce.e2e.pageObjects.addresses;

import com.chairpick.ecommerce.e2e.pageObjects.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CreateAddressPage extends PageObject {

    private final By nameInput = By.cssSelector("input#name");
    private final By streetInput = By.cssSelector("input#address");
    private final By streetTypeInput = By.cssSelector("select#address_type");
    private final By cepInput = By.cssSelector("input#cep");
    private final By neighborhoodInput = By.cssSelector("input#neighborhood");
    private final By numberInput = By.cssSelector("input#number");
    private final By cityInput = By.cssSelector("input#city");
    private final By stateInput = By.cssSelector("input#state");
    private final By countryInput = By.cssSelector("input#country");
    private final By observationsInput = By.cssSelector("textarea#observations");
    private final By defaultAddressCheckbox = By.cssSelector("input#default_address");
    private final By submitButton = By.cssSelector("button[type='submit']");

    public CreateAddressPage(WebDriver driver) {
        super(driver);
    }

    public String fillName(String name) {
        driver.findElement(nameInput).sendKeys(name);
        return driver.findElement(nameInput).getDomProperty("value");
    }

    public String fillStreet(String street) {
        driver.findElement(streetInput).sendKeys(street);
        return driver.findElement(streetInput).getDomProperty("value");
    }

    public String selectStreetType(String streetType) {
        driver.findElement(streetTypeInput).sendKeys(streetType);
        return driver.findElement(streetTypeInput).getDomProperty("value");
    }

    public String fillCep(String cep) {
        driver.findElement(cepInput).sendKeys(cep);
        return driver.findElement(cepInput).getDomProperty("value");
    }

    public String fillNeighborhood(String neighborhood) {
        driver.findElement(neighborhoodInput).sendKeys(neighborhood);
        return driver.findElement(neighborhoodInput).getDomProperty("value");
    }

    public String fillNumber(String number) {
        driver.findElement(numberInput).sendKeys(number);
        return driver.findElement(numberInput).getDomProperty("value");
    }

    public String fillCity(String city) {
        driver.findElement(cityInput).sendKeys(city);
        return driver.findElement(cityInput).getDomProperty("value");
    }

    public String fillState(String state) {
        driver.findElement(stateInput).sendKeys(state);
        return driver.findElement(stateInput).getDomProperty("value");
    }

    public String fillCountry(String country) {
        driver.findElement(countryInput).sendKeys(country);
        return driver.findElement(countryInput).getDomProperty("value");
    }

    public String fillObservations(String observations) {
        driver.findElement(observationsInput).sendKeys(observations);
        return driver.findElement(observationsInput).getDomProperty("value");
    }

    public boolean isSubmitButtonEnabled() {
        String disabledAttribute = driver.findElement(submitButton).getAttribute("disabled");
        return disabledAttribute == null || !disabledAttribute.equals("true");
    }

    public AddressHomePage submit() {
        driver.findElement(submitButton).click();
        return new AddressHomePage(driver);
    }

}

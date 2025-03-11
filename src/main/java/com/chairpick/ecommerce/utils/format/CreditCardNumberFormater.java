package com.chairpick.ecommerce.utils.format;

public class CreditCardNumberFormater {

    public static String format(String creditCardNumber) {
        return creditCardNumber.replaceAll("(\\d{4})(\\d{4})(\\d{4})(\\d{4})", "$1 **** **** ****");
    }
}

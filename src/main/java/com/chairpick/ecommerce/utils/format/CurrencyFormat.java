package com.chairpick.ecommerce.utils.format;

import java.text.NumberFormat;

public class CurrencyFormat {

    public static String format(double value) {
        return String.format("R$ %.2f", value).replace(".", ",");
    }
}

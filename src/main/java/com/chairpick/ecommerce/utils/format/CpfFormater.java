package com.chairpick.ecommerce.utils.format;

public class CpfFormater {

    public static String format(String value) {
        if (value == null || value.length() != 11) {
            return value;
        }
        return value.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}

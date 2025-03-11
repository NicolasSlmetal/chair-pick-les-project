package com.chairpick.ecommerce.utils.format;

public class PhoneFormater {

    public static String format(String phone) {
        return phone.replaceAll("(\\d{5})(\\d{4})", "$1-$2");
    }
}

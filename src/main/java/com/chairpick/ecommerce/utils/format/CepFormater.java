package com.chairpick.ecommerce.utils.format;

public class CepFormater {

    public static String format(String cep) {
        return cep.replaceAll("(\\d{5})(\\d{3})", "$1-$2");
    }
}

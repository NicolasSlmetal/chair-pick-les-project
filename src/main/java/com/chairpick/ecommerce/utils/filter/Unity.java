package com.chairpick.ecommerce.utils.filter;

public enum Unity {
    CENTIMETRO {

        public String process(String number) {
            return number;
        }
    },

    METRO {

        public String process(String number) {
            return String.valueOf(Double.parseDouble(number) * 100);
        }
    },
    GRAMA {

        public String process(String number) {
            return String.valueOf(Double.parseDouble(number) / 1000);
        }
    },
    INCHES {

        public String process(String number) {
            return String.valueOf(Double.parseDouble(number) * 2.54);
        }
    },
    KILOGRAMA {

        public String process(String number) {
            return number;
        }
    };

    public abstract String process(String number);
}
package com.chairpick.ecommerce.io.input;

import java.util.List;

public record OrderInput(Long billingAddressId,
                         Long deliveryAddressId,
                         List<CreditCardPaymentInput> creditCards,
                         List<Long> coupons) {
}

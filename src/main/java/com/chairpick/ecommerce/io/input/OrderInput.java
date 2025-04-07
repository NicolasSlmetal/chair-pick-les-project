package com.chairpick.ecommerce.io.input;

import java.util.List;

public record OrderInput(Long billingAddressId,
                         Long deliveryAddressId,
                         List<CreditCartPaymentInput> creditCards,
                         List<Long> coupons) {
}

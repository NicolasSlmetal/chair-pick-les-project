package com.chairpick.ecommerce.io.input;

import java.time.LocalDate;

public record ItemInput (
        LocalDate entryDate,
        Integer amount,
        Double unitCost,
        SupplierInput supplier

) {
}


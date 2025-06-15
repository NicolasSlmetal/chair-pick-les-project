package com.chairpick.ecommerce.params;

import com.chairpick.ecommerce.model.Category;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.PriceChangeRequest;

import java.util.List;

public record UpsertChairParams(
        Chair chair,
        float[] vector,
        List<Category> categoriesToInsert,
        List<Category> categoriesToRemove,
        PriceChangeRequest priceChangeRequest
) {}


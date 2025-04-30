package com.chairpick.ecommerce.utils.pagination;

import java.util.List;
import java.util.function.Consumer;

public record PageInfo<T>(int totalResults, List<T> entitiesInPage) {

    public void forEach(Consumer<T> consumer) {
        entitiesInPage.forEach(consumer);
    }
}

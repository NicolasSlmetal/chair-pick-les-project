package com.chairpick.ecommerce.utils.pagination;

import java.util.List;

public record PageInfo<T>(Long totalPages, List<T> entitiesInPage) {

}

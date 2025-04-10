package com.chairpick.ecommerce.utils.pagination;

import java.util.List;

public record PageInfo<T>(int totalResults, List<T> entitiesInPage) {

}

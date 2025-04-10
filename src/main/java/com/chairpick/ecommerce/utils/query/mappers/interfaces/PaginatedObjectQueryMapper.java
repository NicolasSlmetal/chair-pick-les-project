package com.chairpick.ecommerce.utils.query.mappers.interfaces;

import com.chairpick.ecommerce.utils.pagination.PageOptions;
import com.chairpick.ecommerce.utils.query.QueryResult;

import java.util.Map;

public interface PaginatedObjectQueryMapper<T> {

    QueryResult parseParameters(Map<String, String> parameters, PageOptions pageOptions);
}

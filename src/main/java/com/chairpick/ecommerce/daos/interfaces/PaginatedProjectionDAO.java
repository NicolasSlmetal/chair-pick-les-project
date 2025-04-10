package com.chairpick.ecommerce.daos.interfaces;

import com.chairpick.ecommerce.utils.pagination.PageInfo;
import com.chairpick.ecommerce.utils.pagination.PageOptions;

import java.util.Map;

public interface PaginatedProjectionDAO <T, U> extends ProjectionDAO<T, U> {
    PageInfo<U> findAndPaginateForProjection(Map<String, String> parameters, PageOptions pageOptions);
}

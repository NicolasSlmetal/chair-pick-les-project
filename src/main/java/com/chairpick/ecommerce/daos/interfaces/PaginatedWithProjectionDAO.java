package com.chairpick.ecommerce.daos.interfaces;

import java.util.List;
import java.util.Map;

public interface PaginatedWithProjectionDAO <T, U> extends GenericPaginatedDAO<T> {

    List<U> findAndMapForProjection(Map<String, String> parameters);
}

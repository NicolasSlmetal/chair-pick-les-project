package com.chairpick.ecommerce.daos.interfaces;

import com.chairpick.ecommerce.utils.pagination.PageInfo;
import com.chairpick.ecommerce.utils.pagination.PageOptions;

import java.util.Map;

public interface GenericPaginatedDAO<T> extends GenericDAO<T> {

    PageInfo<T> findAndPaginate(Map<String, String> parameters, PageOptions pageOptions);

}

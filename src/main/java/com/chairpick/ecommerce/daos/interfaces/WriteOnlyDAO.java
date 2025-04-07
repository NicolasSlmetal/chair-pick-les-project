package com.chairpick.ecommerce.daos.interfaces;

public interface WriteOnlyDAO<T> {

    T insert(T entity);
    T update(T entity);
}

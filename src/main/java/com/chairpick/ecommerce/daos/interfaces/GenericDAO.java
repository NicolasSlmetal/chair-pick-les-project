package com.chairpick.ecommerce.daos.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GenericDAO <T> {
    T save(T entity);
    T update(T entity);
    List<T> findAll();
    Optional<T> findById(Long id);
    List<T> findBy(Map<String, String> parameters);
    void delete(Long id);
}

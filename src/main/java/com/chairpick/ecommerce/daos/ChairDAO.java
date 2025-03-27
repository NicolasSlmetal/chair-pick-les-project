package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.model.Chair;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ChairDAO implements GenericDAO<Chair> {
    @Override
    public Chair save(Chair entity) {
        return null;
    }

    @Override
    public Chair update(Chair entity) {
        return null;
    }

    @Override
    public List<Chair> findAll() {
        return List.of();
    }

    @Override
    public Optional<Chair> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Chair> findBy(Map<String, String> parameters) {
        return List.of();
    }

    @Override
    public void delete(Long id) {

    }
}

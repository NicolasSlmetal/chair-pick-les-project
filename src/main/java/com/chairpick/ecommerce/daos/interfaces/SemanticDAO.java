package com.chairpick.ecommerce.daos.interfaces;

import com.chairpick.ecommerce.projections.SemanticResultProjection;

import java.util.List;

public interface SemanticDAO <T> {

    List<SemanticResultProjection> findByVector(float[] vector);
    Long upsertVectorWithEntityMetadata(T entity, float[] vector);
    void removeById(Long id);
}

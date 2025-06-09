package com.chairpick.ecommerce.daos.interfaces;

import com.chairpick.ecommerce.projections.SemanticResultProjection;
import com.chairpick.ecommerce.utils.filter.FilterObject;

import java.util.List;

public interface SemanticDAO <T> {

    List<SemanticResultProjection> findByVector(float[] vector);
    List<SemanticResultProjection> findByVector(float[] vector, FilterObject filter);
    Long upsertVectorWithEntityMetadata(T entity, float[] vector);
    void removeById(Long id);
}

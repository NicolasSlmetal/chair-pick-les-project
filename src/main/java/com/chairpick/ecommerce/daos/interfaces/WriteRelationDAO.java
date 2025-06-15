package com.chairpick.ecommerce.daos.interfaces;

public interface WriteRelationDAO <L, R> {

    void insertRelation(L leftEntity, R rightEntity);
    void deleteRelation(L leftEntity, R rightEntity);
}

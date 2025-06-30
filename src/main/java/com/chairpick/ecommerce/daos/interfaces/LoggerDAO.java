package com.chairpick.ecommerce.daos.interfaces;

public interface LoggerDAO {


    void logInsert(Object object, Long userId);
    void logUpdate(Object oldObject, Object newObject, Long userId);
}

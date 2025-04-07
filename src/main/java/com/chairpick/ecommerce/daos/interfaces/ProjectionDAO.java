package com.chairpick.ecommerce.daos.interfaces;

import java.util.List;
import java.util.Map;

public interface ProjectionDAO <T, U> extends GenericDAO <T> {

    List<U> findAndMapForProjection(Map<String, String> parameters);
}

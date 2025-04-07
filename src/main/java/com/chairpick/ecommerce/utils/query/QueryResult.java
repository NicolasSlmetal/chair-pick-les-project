package com.chairpick.ecommerce.utils.query;

import java.util.Map;

public record QueryResult(String query, Map<String, String> parameters) {
}

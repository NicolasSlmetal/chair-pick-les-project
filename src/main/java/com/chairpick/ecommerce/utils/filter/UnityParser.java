package com.chairpick.ecommerce.utils.filter;

import java.util.Map;
import java.util.Optional;

public class UnityParser {

    private static Map<String, Unity> unitMap = Map.ofEntries(
            Map.entry("centímetro", Unity.CENTIMETRO),
            Map.entry("centímetros", Unity.CENTIMETRO),
            Map.entry("metro", Unity.METRO),
            Map.entry("metros", Unity.METRO),
            Map.entry("grama", Unity.GRAMA),
            Map.entry("gramas", Unity.GRAMA),
            Map.entry("quilograma", Unity.KILOGRAMA),
            Map.entry("quilogramas", Unity.KILOGRAMA),
            Map.entry("kg", Unity.KILOGRAMA),
            Map.entry("g", Unity.GRAMA),
            Map.entry("cm", Unity.CENTIMETRO),
            Map.entry("m", Unity.METRO)

    );

    public static Optional<Unity> parse(String unity) {
        return Optional.ofNullable(unitMap.get(unity.toLowerCase()));
    }
}

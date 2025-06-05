package com.chairpick.ecommerce.utils.filter;

import org.springframework.context.annotation.Primary;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Primary
public class QdrantTextFilter implements TextQueryFilter {


    List<String> keyExpressions = List.of(
            "preco",
            "preço",
            "valor",
            "altura",
            "largura",
            "comprimento",
            "peso",
            "avaliação",
            "avaliações",
            "estrelas"
    );
    Map<String, String> comparisonOperators = Map.of(
            "menor", "<",
            "maior", ">",
            "igual", "=",
            "diferente", "!=",
            "mais", ">",
            "menos", "<"
    );

    List<String> betweenOperators = List.of(
            "entre"
    );
    @Override
    public FilterObject filterByText(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        List<ValueFilter> filters = new ArrayList<>();
        keyExpressions
                .forEach(
                        key -> {
                            int index = Arrays.asList(words).indexOf(key);
                            if (index > -1) {
                                ValueFilter.ValueFilterBuilder builder = ValueFilter.builder().field(key);
                                //Find the next number occurrence, probably defining the value
                                Pattern numberPattern = Pattern.compile("\\d+(?:\\.\\d+)?");
                                Matcher matcher = numberPattern.matcher(text.substring(index + key.length()));
                                if (matcher.find()) {
                                    String value = matcher.group();
                                    String sentence = text.substring(index, index + key.length() + value.length()).toLowerCase();
                                    Set<String> keys = comparisonOperators.keySet();
                                    Pattern operatorPattern = Pattern.compile(String.join("|", keys));

                                    Matcher operatorMatcher = operatorPattern.matcher(sentence);
                                    if (operatorMatcher.find()) {
                                        String operatorKey = operatorMatcher.group();
                                        builder.operator(comparisonOperators.get(operatorKey))
                                                .value(value);
                                    }
                                }
                                filters.add(builder.build());
                            }
                        }
                );
        return new QdrantFilterObject(filters);
    }
}

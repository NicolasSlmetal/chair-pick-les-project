package com.chairpick.ecommerce.utils.filter;

import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
            "diferente", "!="
    );

    @Override
    public FilterObject filterByText(String text) {
        String[] words = text.split("\\s+");
        List<ValueFilter> filters = new ArrayList<>();
        keyExpressions
                .forEach(
                        key -> {
                            int index = Arrays.asList(words).indexOf(key);
                            if (index > -1) {
                                //Find the next number occurrence, probably defining the value
                                Pattern numberPattern = Pattern.compile("\\d+(?:\\.\\d+)?");
                                Matcher matcher = numberPattern.matcher(text.substring(index + key.length()));
                                if (matcher.find()) {
                                    String value = matcher.group();
                                    String sentence = text.substring(index, index + key.length() + value.length()).toLowerCase();
                                    String[] dividedSentence = sentence.split("\\s+");
                                    int middleIndex = (dividedSentence.length / 2) - 1;
                                    String middleWord = dividedSentence[middleIndex];
                                    if (comparisonOperators.containsKey(middleWord)) {
                                        filters.add(ValueFilter.builder()
                                                        .field(key)
                                                        .operator(comparisonOperators.get(middleWord))
                                                        .value(value)
                                                .build());
                                    }
                                }
                            }
                        }
                );
        return new QdrantFilterObject(filters);
    }
}

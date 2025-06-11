package com.chairpick.ecommerce.utils.filter;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class QdrantTextFilter implements TextQueryFilter {


    private final List<String> keyExpressions = List.of(
            "preco", "preço", "valor", "altura", "largura",
            "comprimento", "peso", "avaliação", "avaliações", "estrelas"
    );

    private final Map<String, String> wordToKeyMap = Map.ofEntries(
            Map.entry("preco", "price"),
            Map.entry("preço", "price"),
            Map.entry("valor", "price"),
            Map.entry("altura", "height"),
            Map.entry("largura", "width"),
            Map.entry("comprimento", "length"),
            Map.entry("peso", "weight"),
            Map.entry("avaliação", "rating"),
            Map.entry("avaliações", "rating"),
            Map.entry("estrelas", "rating")

    );

    private final Map<String, String> comparisonOperators = Map.ofEntries(
            Map.entry("maior_ou_igual", ">="),
            Map.entry("menor_ou_igual", "<="),
            Map.entry("maior", ">"),
            Map.entry("menor", "<"),
            Map.entry("abaixo", "<"),
            Map.entry("acima", ">"),
            Map.entry("mais", ">"),
            Map.entry("menos", "<"),
            Map.entry("igual", "="),
            Map.entry("diferente", "!=")
    );

    private final List<String> betweenOperators = List.of("entre");

    @Override
    public FilterObject filterByText(String text) {
        String lowerText = text.toLowerCase().replace(",", ".").replaceAll("\\s+", " ");
        List<String> clauses = splitIntoClauses(lowerText);
        List<ValueFilter> filters = new ArrayList<>();
        Map<Integer, Boolean> clauseProcessed = new HashMap<>();

        for (String clause : clauses) {
            int index = clauses.indexOf(clause);

            if (clauseProcessed.getOrDefault(index, false)) continue; // Skip if already processed
            Optional<String> fieldOpt = extractField(clause);

            if (fieldOpt.isEmpty()) continue;

            String field = fieldOpt.get();
            Optional<String> operatorOpt = extractOperator(clause);
            String nextClause = null;
            List<String> numbers = extractNumbers(clause);
            if (operatorOpt.isEmpty() && containsBetweenOperator(clause)) {
                int actualIndex = clauses.indexOf(clause);
                if (actualIndex + 1 < clauses.size()) {
                    nextClause = clauses.get(actualIndex + 1);
                    List<String> nextNumbers = extractNumbers(nextClause);
                    if (!nextNumbers.isEmpty()) {
                        numbers.addAll(nextNumbers);
                        clauseProcessed.put(actualIndex + 1, true); // Remove the next clause as it's part of the current filter
                    }
                }
            }
            if (wordToKeyMap.containsKey(field)) {
                field = wordToKeyMap.get(field);
            }

            ValueFilter.ValueFilterBuilder builder = ValueFilter.builder().field(field);

            if (!numbers.isEmpty()) {
                if (operatorOpt.isPresent()) {
                    String operator = comparisonOperators.get(operatorOpt.get());
                    String normalizedNumber = processUnitCases(clause, numbers.getFirst());
                    builder.operator(operator).value(normalizedNumber);
                } else if (containsBetweenOperator(clause) && numbers.size() >= 2 && nextClause != null) {
                    numbers.set(0, processUnitCases(clause, numbers.get(0)));
                    numbers.set(1, processUnitCases(nextClause, numbers.get(1)));
                    builder.operator("between").value(numbers.get(0) + "," + numbers.get(1));
                } else {
                    builder.operator("=").value(numbers.getFirst());
                }
                filters.add(builder.build());
            }
        }

        return new QdrantFilterObject(filters);
    }
    private String processUnitCases(String text, String number) {
        List<String> dividedString = Arrays.asList(text.split("\\s+"));
        int numberIndex = dividedString.indexOf(number);

        if (numberIndex + 1 > dividedString.size()) return number;

        String unity = dividedString.get(numberIndex + 1);

        return UnityParser.parse(unity)
                .map(presentUnity -> presentUnity.process(number))
                .orElse(number);
    }

    private List<String> splitIntoClauses(String text) {

        String[] protectedPhrases = {
                "maior ou igual", "menor ou igual"
        };


        for (String phrase : protectedPhrases) {
            text = text.replaceAll(phrase, phrase.replace("ou", "___PLACEHOLDER___"));
        }

        String[] rawClauses = text.split("\\s+(e|ou|com)\\s+");

        // Restaurar "ou" onde era parte do operador composto
        return Arrays.stream(rawClauses)
                .map(s -> s.replaceAll("___PLACEHOLDER___", "ou"))
                .toList();
    }

    private Optional<String> extractField(String clause) {
        return keyExpressions.stream()
                .filter(clause::contains)
                .max(Comparator.comparingInt(String::length));
    }

    private Optional<String> extractOperator(String clause) {
        String normalized = clause.replaceAll("\\s+", "_");

        return comparisonOperators.keySet().stream()
                .filter(normalized::contains)
                .max(Comparator.comparingInt(String::length));
    }

    private boolean containsBetweenOperator(String clause) {
        return betweenOperators.stream().anyMatch(clause::contains);
    }

    private List<String> extractNumbers(String clause) {
        List<String> numbers = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\d+(\\.\\d+)?").matcher(clause);
        while (matcher.find()) {
            numbers.add(matcher.group());
        }
        return numbers;
    }
}

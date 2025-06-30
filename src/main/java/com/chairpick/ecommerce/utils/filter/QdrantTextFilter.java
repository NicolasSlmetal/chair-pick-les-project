package com.chairpick.ecommerce.utils.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;


@Component
public class QdrantTextFilter implements TextQueryFilter {

    private final ObjectMapper mapper;

    public QdrantTextFilter(ObjectMapper mapper) {
        this.mapper = mapper;
    }


    @Override
    public FilterObject filterByText(String text) {
        try {
            return mapper.readValue(text, QdrantFilterObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}

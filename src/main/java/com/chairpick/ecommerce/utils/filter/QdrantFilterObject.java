package com.chairpick.ecommerce.utils.filter;

public class QdrantFilterObject extends FilterObject {

    public QdrantFilterObject() {
        super();
    }

    public QdrantFilterObject(Boolean relevant, Integer limit, Double[] priceRange, Double[] widthRange, Double[] heightRange, Double[] lengthRange, Double[] weightRange, Double[] ratingRange, String additionalKeywords) {
        super(relevant, limit, priceRange, widthRange, heightRange, lengthRange, weightRange, ratingRange, additionalKeywords);
    }
}

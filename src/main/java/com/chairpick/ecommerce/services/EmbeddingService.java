package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.model.Category;
import com.chairpick.ecommerce.model.Chair;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public float[] generateEmbedding(String text) {
        return embeddingModel.embed(generateEmbeddingTemplate(text));
    }

    public float[] generateEmbeddingForChair(Chair chair) {
        return embeddingModel.embed(generateEmbeddingChairTemplate(chair));
    }

    private static String generateEmbeddingChairTemplate(Chair chair) {
        return String.format("Name: %s\nDescription: %s\nCategories: %s",
                chair.getName(),
                chair.getDescription(),
                chair.getCategories().stream()
                        .map(Category::getName)
                        .reduce((a, b) -> a + ", " + b));
    }

    private static String generateEmbeddingTemplate(String prompt) {
        return String.format("Name: %s\nDescription: %s\nCategories: %s",
                prompt,
                prompt,
                prompt);
    }
}

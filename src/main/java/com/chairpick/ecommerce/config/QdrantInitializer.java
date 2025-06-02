package com.chairpick.ecommerce.config;

import com.chairpick.ecommerce.daos.interfaces.PaginatedProjectionDAO;
import com.chairpick.ecommerce.daos.interfaces.SemanticDAO;
import com.chairpick.ecommerce.model.Category;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.projections.ChairAvailableProjection;
import com.chairpick.ecommerce.services.EmbeddingService;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.context.annotation.Configuration;

import static io.qdrant.client.VectorsFactory.vectors;
import static io.qdrant.client.ValueFactory.value;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Configuration
public class QdrantInitializer {

    private final QdrantClient client;
    private final EmbeddingService embeddingService;
    private final PaginatedProjectionDAO<Chair, ChairAvailableProjection> chairProjectionDAO;
    private final SemanticDAO<Chair> semanticChairDAO;

    public QdrantInitializer(QdrantClient client, OllamaEmbeddingModel embeddingModel, EmbeddingService embeddingService, PaginatedProjectionDAO<Chair, ChairAvailableProjection> chairProjectionDAO, SemanticDAO<Chair> semanticChairDAO) {
        this.client = client;
        this.embeddingService = embeddingService;
        this.chairProjectionDAO = chairProjectionDAO;
        this.semanticChairDAO = semanticChairDAO;
    }

    @PostConstruct
    public void init() throws ExecutionException, InterruptedException {
        if (!client.collectionExistsAsync("chairs").get()) {

            Collections.VectorParams vectorParams = Collections.VectorParams.newBuilder()
                    .setSize(768)
                    .setDistance(Collections.Distance.Cosine)
                    .build();
            client.createCollectionAsync("chairs", vectorParams).get();

            List<ChairAvailableProjection> projections = chairProjectionDAO.findAndMapForProjection(Map.of());
            List<Chair> chairs = projections.stream()
                    .map(projection -> Chair.builder()
                            .id(projection.getId())
                            .name(projection.getName())
                            .description(projection.getDescription())
                            .categories(projection.getCategories())
                            .sellPrice(projection.getPrice())
                            .averageRating(projection.getAverageRating())
                            .height(projection.getHeight())
                            .width(projection.getWidth())
                            .length(projection.getLength())
                            .weight(projection.getWeight())
                            .build())
                    .map(chair -> (Chair) chair)
                    .toList();
            Map<Chair, float[]> vectors = chairs.stream()
                    .collect(Collectors.toMap(chair -> chair, embeddingService::generateEmbeddingForChair));
            vectors.forEach(semanticChairDAO::upsertVectorWithEntityMetadata);
        }
    }
}

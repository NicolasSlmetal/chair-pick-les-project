package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.SemanticDAO;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.projections.SemanticResultProjection;
import com.chairpick.ecommerce.utils.filter.QdrantFilterObject;
import com.google.protobuf.Message;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;
import static io.qdrant.client.ConditionFactory.range;

import io.qdrant.client.grpc.Points.Range;

public class SemanticChairDAO implements SemanticDAO<Chair> {

    private final QdrantClient qdrantClient;

    public SemanticChairDAO(QdrantClient qdrantClient) {
        this.qdrantClient = qdrantClient;
    }

    @Override
    public List<SemanticResultProjection> findByVector(float[] vector) {
        List<SemanticResultProjection> results = new ArrayList<>();
//        QdrantFilterObject object = new QdrantFilterObject(List.of());
//
//        Points.Filter filter = null;
//        if (!object.getFilters().isEmpty()) {
//            Points.Filter.Builder filterBuilder = Points.Filter.newBuilder();
//            object.getFilters().forEach(
//                    valueFilter -> {
//                        Points.Condition.Builder condition = Points.Condition.newBuilder();
//                        valueFilter.getField()
//                    }
//            );
//        }

        float limitScore = 0.8f;
        try {
            float targetScore = 0.9f;

            List<Float> vectorList = new ArrayList<>();
            for (float v : vector) {
                vectorList.add(v);
            }

            while (results.isEmpty() && targetScore > limitScore) {
                List<Points.ScoredPoint> points = qdrantClient.searchAsync(
                        Points.SearchPoints.newBuilder()
                                .setParams(Points.SearchParams.newBuilder()
                                        .setExact(true).build())

                                .setCollectionName("chairs")
                                .addAllVector(vectorList)
                                .setScoreThreshold(targetScore)
                                .setLimit(3L)
                                .build()
                ).get();
                results.addAll(points
                        .stream()
                                .peek(point -> System.out.println("Found point with ID: " + point.getId().getNum() + " and score: " + point.getScore()))
                        .map(point -> SemanticResultProjection.builder()
                                .id(point.getId().getNum())
                                .build()).toList());
                targetScore -= 0.05F;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public Long upsertVectorWithEntityMetadata(Chair entity, float[] vector) {
        Long id = null;
        try {
            qdrantClient.upsertAsync(
                Points.UpsertPoints.newBuilder()
                    .addPoints(Points.PointStruct.newBuilder()
                        .setId(Points.PointId.newBuilder()
                            .setNum(entity.getId())
                            .build())
                        .putAllPayload(generatePayload(entity))
                        .setVectors(vectors(vector))
                        .build())
                        .setCollectionName("chairs")
                    .build()).get();
            id = entity.getId();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return id;
    }



    @Override
    public void removeById(Long id) {

    }

    private static Map<String, JsonWithInt.Value> generatePayload(Chair chair) {
        return Map.of(
                "id", value(chair.getId()),
                "price", value(chair.getSellPrice()),
                "rating", value(chair.getAverageRating()),
                "height", value(chair.getHeight()),
                "width", value(chair.getWidth()),
                "length", value(chair.getLength()),
                "weight", value(chair.getWeight())
        );
    }
}

package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.SemanticDAO;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.projections.SemanticResultProjection;
import com.chairpick.ecommerce.utils.filter.FilterObject;
import com.chairpick.ecommerce.utils.filter.QdrantFilterObject;
import com.google.protobuf.Message;
import io.qdrant.client.ConditionFactory;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;

import java.util.ArrayList;
import java.util.Arrays;
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
    public List<SemanticResultProjection> findByVector(float[] vector, FilterObject filter) {
        List<SemanticResultProjection> results = new ArrayList<>();
        QdrantFilterObject object = new QdrantFilterObject(filter.getFilters());

        List<QdrantFilterObject.QdrantFilterParam> qdrantFilters = object.toObjectFilter();

        Points.Filter.Builder filterBuilder = Points.Filter.newBuilder();
        if (!object.getFilters().isEmpty()) {
            qdrantFilters.forEach(qdrantFilterParam -> {
                Range range = getRangeByComparator(qdrantFilterParam.getRange());
                filterBuilder.addMust(
                        Points.Condition.newBuilder()
                                .setField(Points.FieldCondition.newBuilder()
                                        .setKey(qdrantFilterParam.getKey())
                                        .setRange(range)
                                        .build())
                                .build()
                );
            });
        }

        Points.Filter qdrantFilter = filterBuilder.build();

        float limitScore = 0.70f;
        try {
            float targetScore = 0.8f;

            List<Float> vectorList = new ArrayList<>();
            for (float v : vector) {
                vectorList.add(v);
            }

            while (results.isEmpty() && targetScore > limitScore) {
                List<Points.ScoredPoint> points = qdrantClient.searchAsync(
                        Points.SearchPoints.newBuilder()
                                .setParams(Points.SearchParams.newBuilder()
                                        .setExact(true).build())
                                .setFilter(qdrantFilter)
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

            //Trying to fetch only with filters
            if (results.isEmpty()) {
                float[] emptyVector = new float[vector.length];
                Arrays.fill(emptyVector, 0.0f);
                List<Float> vectorListEmpty = new ArrayList<>();
                for (float v : emptyVector) {
                    vectorListEmpty.add(v);
                }

                List<Points.ScoredPoint> points = qdrantClient.searchAsync(
                        Points.SearchPoints.newBuilder()
                                .addAllVector(vectorListEmpty)
                                .setParams(Points.SearchParams.newBuilder()
                                        .setExact(true).build())
                                .setFilter(qdrantFilter)
                                .setCollectionName("chairs")
                                .setLimit(3L)
                                .build()
                ).get();
                results.addAll(points
                        .stream()
                        .peek(point -> System.out.println("Found point with ID: " + point.getId().getNum() + " and score: " + point.getScore()))
                        .map(point -> SemanticResultProjection.builder()
                                .id(point.getId().getNum())
                                .build()).toList());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return results;
    }

    private Range getRangeByComparator(QdrantFilterObject.QdrantFilterParam.QdrantFilterRange range) {
        Range.Builder rangeBuilder = Range.newBuilder();
        if (range.getGte() != null) {
            rangeBuilder.setGte(range.getGte());
        }
        if (range.getLte() != null) {
            rangeBuilder.setLte(range.getLte());
        }
        if (range.getGt() != null) {
            rangeBuilder.setGt(range.getGt());
        }
        if (range.getLt() != null) {
            rangeBuilder.setLt(range.getLt());
        }

        if (range.getEq() != null) {
            rangeBuilder.setGte(range.getEq());
            rangeBuilder.setLte(range.getEq());
        }

        return rangeBuilder.build();

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

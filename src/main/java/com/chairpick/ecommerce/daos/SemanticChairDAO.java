package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.SemanticDAO;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.projections.SemanticResultProjection;
import com.chairpick.ecommerce.utils.filter.FilterObject;
import com.chairpick.ecommerce.utils.filter.QdrantFilterObject;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Field;
import com.google.protobuf.Message;
import io.qdrant.client.ConditionFactory;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;
import static io.qdrant.client.ConditionFactory.range;

import io.qdrant.client.grpc.Points.Range;
import org.openqa.selenium.InvalidArgumentException;

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
        QdrantFilterObject qdrantFilterText = ensureFilterIsQdrantFilter(filter);


        Points.Filter.Builder filterBuilder = Points.Filter.newBuilder();
        Map<String, Double[]> filtersMap = parseParamsFromFilter(qdrantFilterText);
        for (Map.Entry<String, Double[]> entry : filtersMap.entrySet()) {
            String key = entry.getKey();
            Double[] rangeValues = entry.getValue();
            if (rangeValues == null || rangeValues.length == 0) {
                continue;
            }

            Range.Builder builder = Range.newBuilder();
            var range = range(key, getRangeByProvidedValues(builder, rangeValues));
            filterBuilder.addMust(range);
        }

        Points.Filter qdrantFilter = filterBuilder.build();

        float limitScore = 0.80f;
        try {
            float targetScore = 0.90f;

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
                                .setLimit(qdrantFilterText.getLimit() != null ? qdrantFilterText.getLimit().longValue() : 3L)
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

    private static Map<String, Double[]> parseParamsFromFilter(QdrantFilterObject object) {
        Map<String, Double[]> filtersMap = new HashMap<>();

        if (object.getPriceRange() != null)
            filtersMap.put("price", object.getPriceRange());

        if (object.getRatingRange() != null)
            filtersMap.put("rating", object.getRatingRange());

        if (object.getHeightRange() != null)
            filtersMap.put("height", object.getHeightRange());

        if (object.getWidthRange() != null)
            filtersMap.put("width", object.getWidthRange());

        if (object.getLengthRange() != null)
            filtersMap.put("length", object.getLengthRange());

        if (object.getWeightRange() != null)
            filtersMap.put("weight", object.getWeightRange());
        return filtersMap;
    }

    private static QdrantFilterObject ensureFilterIsQdrantFilter(FilterObject filter) {
        if (filter instanceof QdrantFilterObject qdrantFilterObject) return qdrantFilterObject;

        throw new InvalidArgumentException("Cannot use this filter with Qdrant, it is not a QdrantFilterObject");
    }

    private Range getRangeByProvidedValues(Range.Builder builder, Double[] rangeValues) {
        return builder.setLte(rangeValues[1]).setGte(rangeValues[0]).build();
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

package com.chairpick.ecommerce.e2e.utils;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.VectorsFactory;
import io.qdrant.client.grpc.Points;
import org.springframework.ai.embedding.EmbeddingModel;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class QdrantInitializer {

    private final QdrantClient client;
    private final DatabaseSeeder seeder;
    private final EmbeddingModel embeddingModel;

    public QdrantInitializer(QdrantClient client, DatabaseSeeder seeder, EmbeddingModel embeddingModel) {
        this.client = client;
        this.seeder = seeder;
        this.embeddingModel = embeddingModel;
    }

    public void seedDefaultProducts() throws ExecutionException, InterruptedException {
        String newPricingGroupQuery = """
                INSERT INTO tb_pricing_group (
                    pgr_name,
                    pgr_percent_value
                )
                VALUES
                (:name, :value) RETURNING pgr_id;
                """;
        Map<String, Object> pricingGroupParams = Map.of(
                "name", "Any",
                "value", 1.20
        );
        Long newPricingGroupId = seeder.executeReturningId(newPricingGroupQuery, pricingGroupParams);

        String chairInsert = "INSERT INTO tb_chair (" +
                "chr_name, " +
                "chr_description, " +
                "chr_sell_price, " +
                "chr_width, " +
                "chr_height, " +
                "chr_length, " +
                "chr_weight, " +
                "chr_pricing_group_id, " +
                "chr_average_rating) " +
                "VALUES " +
                "(:name, :description, :sellPrice, :width, :height, :length, :weight, :pricingGroupId, :averageRating) RETURNING chr_id;";

        Map<String, Object> chairParams = Map.of(
                "name", "Cadeira Ergonômica",
                "description", "Cadeira ergonômica com suporte lombar ajustável, ideal para longas horas de trabalho.",
                "sellPrice", 10.0,
                "width", 50.0,
                "height", 10.0,
                "length", 50.0,
                "weight", 39.0,
                "pricingGroupId", newPricingGroupId,
                "averageRating", BigDecimal.valueOf(4.5).setScale(2, RoundingMode.HALF_UP)
        );

        String supplierQuery = """
                INSERT INTO tb_supplier
                (sup_name)
                VALUES
                (:name) RETURNING sup_id;
                """;

        Long chairId = seeder.executeReturningId(chairInsert, chairParams);
        Long supplierId = seeder.executeReturningId(supplierQuery, Map.of("name", "Supplier 2"));
        String itemQuery = """
                INSERT INTO tb_item
                (itm_chair_id, 
                itm_entry_date,
                itm_amount,
                itm_reserved,
                itm_unit_cost,
                itm_supplier_id)
                VALUES
                (:chairId,
                :entryDate,
                :amount,
                :reserved,
                :unitCost,
                :supplierId);
                """;
        Map<String, Object> itemParams = Map.of(
                "chairId", chairId,
                "entryDate", LocalDate.now(),
                "amount", 10,
                "reserved", 0,
                "unitCost", BigDecimal.valueOf(50.0).setScale(2, RoundingMode.HALF_UP),
                "supplierId", supplierId
        );
        seeder.execute(itemQuery, itemParams);
        String categoryQuery = """
                INSERT INTO tb_category
                (cat_name)
                VALUES
                (:name) RETURNING cat_id;
                """;
        Long categoryId = seeder.executeReturningId(categoryQuery, Map.of("name", "Category 2"));
        String categoriesChairQuery = """
                INSERT INTO tb_chair_category
                (chc_chair_id,
                chc_category_id)
                VALUES
                (:chairId,
                :categoryId);
                """;
        Map<String, Object> categoriesChairParams = Map.of(
                "chairId", chairId,
                "categoryId", categoryId
        );


        seeder.execute(categoriesChairQuery, categoriesChairParams);

        float[] embed = embeddingModel.embed(generateEmbeddingTemplate("Cadeira Ergonômica",
                        "Cadeira ergonômica com suporte lombar ajustável, ideal para longas horas de trabalho.",
                        "ergonômica, escritório, conforto"));


        client.upsertAsync(Points.UpsertPoints.newBuilder()
                        .setCollectionName("chairs")
                        .addPoints(Points.PointStruct.newBuilder()
                                .setVectors(VectorsFactory.vectors(embed))
                                .setId(Points.PointId.newBuilder()
                                        .setNum(chairId).build())
                                .build())
                        .build()).get();
    }


    private static String generateEmbeddingTemplate(String name, String description, String categories) {
        return String.format("Name: %s\nDescription: %s\nCategories: %s",
                name,
                description,
                categories).toLowerCase();
    }
}

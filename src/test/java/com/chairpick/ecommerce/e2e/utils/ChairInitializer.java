package com.chairpick.ecommerce.e2e.utils;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

public class ChairInitializer {

    private final DatabaseSeeder seeder;

    public ChairInitializer(DatabaseSeeder seeder) {
        this.seeder = seeder;
    }

    public void seedDefaultProducts() {
        String newPricingGroupQuery = """
                INSERT INTO tb_pricing_group (
                    pgr_name,
                    pgr_percent_value
                )
                VALUES
                (:name, :value) RETURNING pgr_id;
                """;
        Map<String, Object> pricingGroupParams = Map.of(
                "name",
                "Any",
                "value",
                1.20
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
                "(:name, " +
                ":description, " +
                ":sellPrice, " +
                ":width, " +
                ":height, " +
                ":length, " +
                ":weight, " +
                ":pricingGroupId, " +
                ":averageRating) RETURNING chr_id;";
        Map<String, Object> chairParams = Map.of(
                "name", "Chair 1",
                "description", "Chair 1 description",
                "sellPrice", 10.0,
                "width", 50.0,
                "height", 10.0,
                "length", 50.0,
                "weight", 39.0,
                "pricingGroupId", newPricingGroupId,
                "averageRating", 4.1
        );
        System.out.println(chairParams);
        Long chairId = seeder.executeReturningId(chairInsert, chairParams);
        String supplierQuery = """
                INSERT INTO tb_supplier
                (sup_name)
                VALUES
                (:name) RETURNING sup_id;
                """;
        Long supplierId = seeder.executeReturningId(supplierQuery, Map.of("name", "Supplier"));
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
                "unitCost", 50.0,
                "supplierId", supplierId
        );
        seeder.execute(itemQuery, itemParams);

        String categoryQuery = """
                INSERT INTO tb_category
                (cat_name)
                VALUES
                (:name) RETURNING cat_id;
                """;
        Long categoryId = seeder.executeReturningId(categoryQuery, Map.of("name", "Category"));

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
    }
}

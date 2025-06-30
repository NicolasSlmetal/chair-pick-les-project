package com.chairpick.ecommerce.daos.registry;

import com.chairpick.ecommerce.model.*;
import com.chairpick.ecommerce.model.payment.strategy.PaymentStrategy;

import java.util.Map;

public class EntitiesTables {

    private static final Map<Class<?>, String> entitiesTables = Map.ofEntries(
            Map.entry(Chair.class, "tb_chair"),
            Map.entry(Cart.class, "tb_cart"),
            Map.entry(Order.class, "tb_order"),
            Map.entry(PaymentStrategy.class, "tb_order_payment"),
            Map.entry(Item.class, "tb_item"),
            Map.entry(OrderItem.class, "tb_order_item"),
            Map.entry(Category.class, "tb_category"),
            Map.entry(PriceChangeRequest.class, "tb_price_change_request"),
            Map.entry(PricingGroup.class, "tb_pricing_group"),
            Map.entry(ChairStatusChange.class, "tb_chair_status_change"),
            Map.entry(User.class, "tb_user"),
            Map.entry(Customer.class, "tb_customer"),
            Map.entry(Address.class, "tb_address"),
            Map.entry(CreditCard.class, "tb_credit_card"),
            Map.entry(Supplier.class, "tb_supplier"),
            Map.entry(Coupon.class, "tb_coupon"),
            Map.entry(Swap.class, "tb_item_swap")
    );


    public static String getTableName(Class<?> entityClass) {
        return entitiesTables.getOrDefault(entityClass, "unknown_table");
    }
}

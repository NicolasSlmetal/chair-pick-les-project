package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.factories.PaymentDAOFactory;
import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.daos.interfaces.OrderPaymentDAO;
import com.chairpick.ecommerce.daos.interfaces.ProjectionDAO;
import com.chairpick.ecommerce.daos.interfaces.WriteOnlyDAO;
import com.chairpick.ecommerce.model.*;
import com.chairpick.ecommerce.model.enums.OrderStatus;
import com.chairpick.ecommerce.projections.CartItemSummaryProjection;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {

    private final GenericDAO<Order> orderDAO;
    private final GenericDAO<Item> itemDAO;
    private final GenericDAO<Cart> cartDAO;
    private final WriteOnlyDAO<OrderItem> orderItemDAO;
    private final PaymentDAOFactory paymentDAOFactory;

    public OrderRepository(GenericDAO<Order> orderDAO, GenericDAO<Item> itemDAO, GenericDAO<Cart> cartDAO, WriteOnlyDAO<OrderItem> orderItemDAO, PaymentDAOFactory paymentDAOFactory) {
        this.orderDAO = orderDAO;
        this.itemDAO = itemDAO;
        this.cartDAO = cartDAO;
        this.orderItemDAO = orderItemDAO;
        this.paymentDAOFactory = paymentDAOFactory;
    }


    @Transactional
    public Order saveOrderAndUpdateStock(Order order, List<Cart> cartList) {
        Order savedOrder  = orderDAO.save(order);

        cartList
                .forEach(cart -> cartDAO.delete(cart.getId()));

        order
                .getItems()
                .stream()
                .map(OrderItem::getItem)
                .forEach(itemDAO::update);

        order
                .getItems()
                .stream()
                .peek(orderItem -> orderItem.setOrder(savedOrder))
                .forEach(orderItemDAO::insert);

        OrderPaymentDAO paymentDAO = paymentDAOFactory.getOrderPaymentDAO(order.getPayment());

        paymentDAO.save(order.getPayment(), order.getId());

        return order;
    }

    public List<Order> findAllByCustomer(Customer customer, Map<String, String> parameters) {
        parameters.put("customer_id", customer.getId().toString());
        if (parameters.containsKey("status")) {
            parameters.put("status", parameters.get("status").toUpperCase());
        }
        return orderDAO.findBy(parameters);
    }
}

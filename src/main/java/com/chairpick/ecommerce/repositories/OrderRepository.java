package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.CompositePaymentDAO;
import com.chairpick.ecommerce.daos.registry.PaymentDAORegistry;
import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.daos.interfaces.OrderPaymentDAO;
import com.chairpick.ecommerce.daos.interfaces.WriteOnlyDAO;
import com.chairpick.ecommerce.io.output.CompositePaymentDTO;
import com.chairpick.ecommerce.io.output.PaymentDTO;
import com.chairpick.ecommerce.model.*;
import com.chairpick.ecommerce.model.enums.OrderStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class OrderRepository {

    private final GenericDAO<Order> orderDAO;
    private final GenericDAO<Item> itemDAO;
    private final GenericDAO<Cart> cartDAO;
    private final GenericDAO<Coupon> couponDAO;
    private final GenericDAO<OrderItem> orderItemDAO;
    private final PaymentDAORegistry paymentDAORegistry;

    public OrderRepository(GenericDAO<Order> orderDAO, GenericDAO<Item> itemDAO, GenericDAO<Cart> cartDAO, GenericDAO<Coupon> couponDAO, GenericDAO<OrderItem> orderItemDAO, PaymentDAORegistry paymentDAORegistry) {
        this.orderDAO = orderDAO;
        this.itemDAO = itemDAO;
        this.cartDAO = cartDAO;
        this.couponDAO = couponDAO;
        this.orderItemDAO = orderItemDAO;
        this.paymentDAORegistry = paymentDAORegistry;
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
                .forEach(orderItemDAO::save);

        OrderPaymentDAO paymentDAO = paymentDAORegistry.getOrderPaymentDAO(order.getPayment());

        paymentDAO.save(order.getPayment(), order.getId());

        return order;
    }

    @Transactional
    public Order saveOrderAndUpdateStock(Order order, List<Cart> cartList, Coupon swapCoupon) {
        saveOrderAndUpdateStock(order, cartList);
        couponDAO.save(swapCoupon);
        return order;
    }

    public List<Order> findAllOrders(Map<String, String> parameters) {
        if (parameters.containsKey("status")) {
            parameters.put("status", parameters.get("status").toUpperCase());
        }
        if (parameters.isEmpty()) {
            return orderDAO.findAll();
        }
        return orderDAO.findBy(parameters);
    }

    public Optional<Order> findById(Long orderId) {
        return orderDAO.findById(orderId);
    }

    public List<OrderItem> findAllOrderItems(Order order) {
        return orderItemDAO.findBy(Map.of("order_id", order.getId().toString()));
    }

    public List<Order> findAllByCustomer(Customer customer, Map<String, String> parameters) {
        parameters.put("customer_id", customer.getId().toString());
        if (parameters.containsKey("status")) {
            parameters.put("status", parameters.get("status").toUpperCase());
        }
        return orderDAO.findBy(parameters);
    }

    public Optional<OrderItem> findOrderItemById(Long orderItemId) {
        return orderItemDAO.findById(orderItemId);
    }

    public Optional<PaymentDTO> findPaymentByOrder(Order order) {
        Optional<CompositePaymentDAO> optionalCompositePaymentDAO = paymentDAORegistry.getCompositeDAO();
        if (optionalCompositePaymentDAO.isPresent()) {
            CompositePaymentDAO compositePaymentDAO = optionalCompositePaymentDAO.get();
            return Optional.of(compositePaymentDAO.findByOrderId(order.getId()));
        }
        return Optional.empty();
    }

    @Transactional
    public Order updateOrderStatus(Order order) {
        orderDAO.update(order);
        if (order.getStatus().equals(OrderStatus.APPROVED) ||
                order.getStatus().equals(OrderStatus.REPROVED)
        || order.getStatus().equals(OrderStatus.PENDING)) {
            order.getItems()
                    .forEach(
                            orderItemDAO::update
                    );
        }
        return order;
    }

    public OrderItem updateOrderItemStatus(OrderItem orderItem) {
        return orderItemDAO.update(orderItem);
    }
}

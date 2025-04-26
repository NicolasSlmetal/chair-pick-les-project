package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.OrderPaymentDAO;
import com.chairpick.ecommerce.io.output.CompositePaymentDTO;
import com.chairpick.ecommerce.io.output.PaymentDTO;
import com.chairpick.ecommerce.io.output.PaymentType;
import com.chairpick.ecommerce.model.payment.strategy.PaymentStrategy;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class CompositePaymentDAO implements OrderPaymentDAO {

    final List<OrderPaymentDAO> orderPaymentDAOs;

    public CompositePaymentDAO(List<OrderPaymentDAO> orderPaymentDAOs) {
        this.orderPaymentDAOs = orderPaymentDAOs;
    }

    @Override
    public final PaymentStrategy save(PaymentStrategy payment, Long orderId) {
        for (OrderPaymentDAO orderPaymentDAO : orderPaymentDAOs) {
            orderPaymentDAO.save(payment, orderId);
        }
        return payment;
    }

    @Override
    public final PaymentDTO findByOrderId(Long orderId) {
        Map<PaymentType, PaymentDTO> payments = orderPaymentDAOs.stream()
                .map(orderPaymentDAO -> orderPaymentDAO.findByOrderId(orderId))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(PaymentDTO::getPaymentType, paymentDTO -> paymentDTO));
        return new CompositePaymentDTO(orderId, payments);
    }

    @Override
    public void delete(Long orderId) {
        orderPaymentDAOs
                .forEach(dao -> dao.delete(orderId));
    }

    public void add(OrderPaymentDAO orderPaymentDAO) {
        orderPaymentDAOs.add(orderPaymentDAO);
    }

    public void remove(OrderPaymentDAO orderPaymentDAO) {
        orderPaymentDAOs.remove(orderPaymentDAO);
    }
}

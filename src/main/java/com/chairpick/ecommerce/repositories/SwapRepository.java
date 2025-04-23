package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class SwapRepository {

    private final GenericDAO<Swap> swapDAO;
    private final GenericDAO<OrderItem> orderItemDAO;
    private final GenericDAO<Item> itemDAO;
    private final GenericDAO<Coupon> couponDAO;

    public SwapRepository(GenericDAO<Swap> swapDAO, GenericDAO<OrderItem> orderItemDAO, GenericDAO<Item> itemDAO, GenericDAO<Coupon> couponDAO) {
        this.swapDAO = swapDAO;
        this.orderItemDAO = orderItemDAO;
        this.itemDAO = itemDAO;
        this.couponDAO = couponDAO;
    }

    @Transactional
    public Swap save(Swap swap) {

        Swap savedSwap = swapDAO.save(swap);
        orderItemDAO.update(savedSwap.getOrderItem());

        return savedSwap;
    }

    public List<Swap> findAllByOrder(Order order) {
        return swapDAO.findBy(Map.of("order_id", order.getId().toString()));
    }

    public Optional<Swap> findById(Long id) {
        return swapDAO.findById(id);
    }

    @Transactional
    public Swap updateStatus(Swap swap) {

        Swap updatedSwap = swapDAO.update(swap);
        orderItemDAO.update(updatedSwap.getOrderItem());
        return updatedSwap;
    }

    @Transactional
    public Swap confirmSwap(Swap swap, Coupon coupon) {

        Swap updatedSwap = swapDAO.update(swap);
        orderItemDAO.update(updatedSwap.getOrderItem());
        itemDAO.update(updatedSwap.getOrderItem().getItem());
        couponDAO.save(coupon);
        return updatedSwap;
    }


}

package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.Coupon;
import com.chairpick.ecommerce.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CouponRepository {

    private final GenericDAO<Coupon> couponDAO;

    public CouponRepository(GenericDAO<Coupon> couponDAO) {
        this.couponDAO = couponDAO;
    }

    public List<Coupon> findCouponsByCustomer(Customer customer) {
        return couponDAO.findBy(Map.of("customer_id", customer.getId().toString()));
    }

    public List<Coupon> findCouponsByCustomer(Long customerId) {
        return couponDAO.findBy(Map.of("customer_id", customerId.toString()));
    }

    public Optional<Coupon> findCouponById(Long id) {
        return couponDAO.findById(id);
    }
}

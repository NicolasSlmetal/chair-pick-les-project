package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.daos.interfaces.ProjectionDAO;
import com.chairpick.ecommerce.model.*;
import com.chairpick.ecommerce.projections.CartItemSummaryProjection;
import com.chairpick.ecommerce.model.enums.CartItemStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class CartRepository {

    private final ProjectionDAO<Cart, CartItemSummaryProjection> cartDAO;
    private final GenericDAO<Item> itemDAO;

    public CartRepository(ProjectionDAO<Cart, CartItemSummaryProjection> cartDAO, GenericDAO<Item> itemDAO) {
        this.cartDAO = cartDAO;
        this.itemDAO = itemDAO;
    }

    public List<Cart> findCartByCustomerAndChair(Customer customer, Chair chair) {
        Map<String, String> parameters = Map.of(
                "customer_id", customer.getId().toString(),
                "itm_chair_id", chair.getId().toString()
        );
        List<Cart> carts = cartDAO.findBy(parameters);
        carts.forEach(cart -> cart.setCustomer(customer));
        return carts;
    }

    public List<CartItemSummaryProjection> summarizeByCustomer(Customer customer) {
        Map<String, String> parameters = Map.of("customer_id", customer.getId().toString());
        List<CartItemSummaryProjection> carts = cartDAO.findAndMapForProjection(parameters);
        carts.forEach(cart -> cart.setCustomer(customer));
        return carts;
    }

    public List<Cart> findByCustomer(Customer customer) {
        Map<String, String> parameters = Map.of("customer_id", customer.getId().toString());
        List<Cart> carts = cartDAO.findBy(parameters);
        carts.forEach(cart -> cart.setCustomer(customer));
        return carts;
    }

    public List<Cart> findByCustomerAndStatus(Customer customer, CartItemStatus status) {
        Map<String, String> parameters = Map.of(
                "customer_id", customer.getId().toString(),
                "item_status", status.toString()
        );
        List<Cart> carts = cartDAO.findBy(parameters);
        carts.forEach(cart -> cart.setCustomer(customer));
        return carts;
    }

    @Transactional
    public Cart addItemToCart(Cart cart) {
        itemDAO.update(cart.getItem());
        return cartDAO.save(cart);
    }

    @Transactional
    public List<Cart> updateCartItems(List<Cart> carts) {
        List<Cart> savedCarts = new ArrayList<>();
        carts.forEach(cart -> {
            itemDAO.update(cart.getItem());
            if (cart.getId() == null) {
                savedCarts.add(cartDAO.save(cart));
            } else if (cart.getStatus().equals(CartItemStatus.ACTIVE)) {
                savedCarts.add(cartDAO.update(cart));
            } else {
                cartDAO.delete(cart.getId());
            }
        });
        return savedCarts;
    }

    @Transactional
    public List<Cart> batchUpdateCarts(List<Cart> carts) {
        carts.forEach(cartDAO::update);
        carts.forEach(cart-> itemDAO.update(cart.getItem()));
        return carts;
    }

    @Transactional
    public void deleteCartItems(List<Cart> carts) {
        carts.forEach(cart -> {
            itemDAO.update(cart.getItem());
            cartDAO.delete(cart.getId());
        });
    }
}

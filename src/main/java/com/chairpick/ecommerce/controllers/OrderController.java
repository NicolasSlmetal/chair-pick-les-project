package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.io.input.OrderInput;
import com.chairpick.ecommerce.io.input.OrderStatusInput;
import com.chairpick.ecommerce.io.output.PaymentDTO;
import com.chairpick.ecommerce.io.output.TotalValueDTO;
import com.chairpick.ecommerce.model.Coupon;
import com.chairpick.ecommerce.model.CreditCard;
import com.chairpick.ecommerce.model.Order;
import com.chairpick.ecommerce.model.OrderItem;
import com.chairpick.ecommerce.model.enums.CouponType;
import com.chairpick.ecommerce.projections.CartItemSummaryProjection;
import com.chairpick.ecommerce.projections.OrderReportByChairs;
import com.chairpick.ecommerce.services.CartService;
import com.chairpick.ecommerce.services.CouponService;
import com.chairpick.ecommerce.services.CreditCardService;
import com.chairpick.ecommerce.services.OrderService;
import com.chairpick.ecommerce.utils.pagination.PageInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final CreditCardService creditCardService;
    private final CartService cartService;
    private final CouponService couponService;

    public OrderController(OrderService orderService, CreditCardService creditCardService, CartService cartService, CouponService couponService) {
        this.orderService = orderService;
        this.creditCardService = creditCardService;
        this.cartService = cartService;
        this.couponService = couponService;
    }

    @GetMapping("customers/{customerId}/orders")
    public ResponseEntity<PageInfo<Order>> findAllByCustomer(@PathVariable Long customerId, @RequestParam Map<String, String> params) {
        PageInfo<Order> orders = orderService.findAllByCustomer(customerId, params);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("customers/{customerId}/orders/payment")
    public ModelAndView confirmPayment(@PathVariable Long customerId, @CookieValue("deliveryAddressId") Long deliveryAddressId, @CookieValue("billingAddressId") Long billingAddressId) {

        List<CreditCard> creditCards = creditCardService.findCreditCardByCustomerId(customerId);
        Map<CartItemSummaryProjection, TotalValueDTO> totalAmountMap = cartService.showCartConfirmationWithSelectedAddress(customerId, deliveryAddressId);
        Map<CouponType, List<Coupon>> coupons = couponService.findCouponsByCustomer(customerId);

        ModelAndView view = new ModelAndView("orders/payment.html");
        view.addObject("customerId", customerId);
        view.addObject("coupons", coupons);
        view.addObject("creditCards", creditCards);
        view.addObject("deliveryAddressId", deliveryAddressId);
        view.addObject("billingAddressId", billingAddressId);
        view.addObject("totalValue", cartService.getTotalValueWithFreight(totalAmountMap));
        return view;
    }

    @GetMapping("admin/orders")
    public ModelAndView findAllOrders(@RequestParam Map<String, String> params) {
        ModelAndView view = new ModelAndView("orders/index.html");
        view.addObject("orders", orderService.findAllOrders(params));
        return view;
    }

    @GetMapping("admin/orders/{orderId}/payment")
    public ModelAndView findOrderPayment(@PathVariable Long orderId) {
        ModelAndView view = new ModelAndView("orders/payment-status.html");
        PaymentDTO orderPayment = orderService.findOrderPayment(orderId);
        view.addObject("payment", orderPayment);
        return view;
    }

    @GetMapping("admin/orders/{orderId}")
    public ModelAndView findOrderItems(@PathVariable Long orderId) {
        ModelAndView view = new ModelAndView("orders/items.html");
        List<OrderItem> orderItems = orderService.findOrderItems(orderId);

        view.addObject("items", orderItems);
        view.addObject("orderId", orderId);
        return view;
    }

    @GetMapping("admin/orders/dashboard")
    public ModelAndView redirectToOrdersDashboard() {
        ModelAndView view = new ModelAndView("orders/dashboard.html");
        view.addObject("pageTitle", "Orders Dashboard");
        return view;
    }

    @GetMapping("admin/orders/reports")
    public ResponseEntity<List<OrderReportByChairs>> findOrderReportsByChair(@RequestParam(
            name ="startDate", defaultValue = "#{T(java.time.LocalDate).now().minusMonths(1)}") LocalDate startDate,
                                                                             @RequestParam(name = "endDate",
                                                                             defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate endDate) {
        List<OrderReportByChairs> reports = orderService.findAllReportsByChair(startDate, endDate);
        return ResponseEntity.ok(reports);
    }

    @PostMapping("customers/{customerId}/orders")
    public ResponseEntity<Order> createOrder(@PathVariable Long customerId, @RequestBody OrderInput orderInput) {
        return new ResponseEntity<>(orderService.createOrder(customerId, orderInput), HttpStatus.CREATED);
    }

    @PatchMapping("admin/orders/{orderId}/items/{itemId}/status")
    public ResponseEntity<OrderItem> updateOrderItemStatus(@PathVariable("orderId") Long orderId, @PathVariable("itemId") Long orderItemId, @RequestBody OrderStatusInput statusInput) {
        OrderItem orderItem = orderService.updateOrderItemStatus(orderItemId, statusInput);
        return ResponseEntity.ok(orderItem);
    }

    @PatchMapping("admin/orders/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusInput statusInput) {
        Order order = orderService.updateOrderStatus(orderId, statusInput);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("customers/{customerId}/orders/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long customerId, @PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

}

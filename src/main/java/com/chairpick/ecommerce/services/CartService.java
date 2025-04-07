package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.AmountExceedsException;
import com.chairpick.ecommerce.exceptions.EntityNotFoundException;
import com.chairpick.ecommerce.exceptions.OptimisticLockException;
import com.chairpick.ecommerce.projections.CartItemSummaryProjection;
import com.chairpick.ecommerce.model.*;
import com.chairpick.ecommerce.io.input.UpdateCartItemInput;
import com.chairpick.ecommerce.io.output.FreightValueDTO;
import com.chairpick.ecommerce.io.output.TotalValueDTO;
import com.chairpick.ecommerce.model.enums.CartItemStatus;
import com.chairpick.ecommerce.repositories.AddressRepository;
import com.chairpick.ecommerce.repositories.CartRepository;
import com.chairpick.ecommerce.repositories.ChairRepository;
import com.chairpick.ecommerce.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final ChairRepository chairRepository;
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final FreightCalculatorService freightCalculatorService;

    public CartService(ChairRepository chairRepository,
                       CustomerRepository customerRepository,
                       CartRepository cartRepository,
                       AddressRepository addressRepository,
                       FreightCalculatorService freightCalculatorService) {
        this.chairRepository = chairRepository;
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.freightCalculatorService = freightCalculatorService;
    }

    public Cart addItemToCart(Long customerId, Long chairId) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("No customer found with user id " + customerId));

        Chair chair = chairRepository
                .findById(chairId)
                .orElseThrow(() -> new EntityNotFoundException("Chair not found with id: " + chairId));

        Item oldestItem = chair.getItems()
                .stream()
                .min(Comparator.comparing(Item::getEntryDate))
                .orElseThrow( () -> new EntityNotFoundException("No items found for chair with id: " + chairId));

        Cart cart = Cart.builder()
                .item(oldestItem)
                .price(chair.getSellPrice())
                .amount(1)
                .limit(chair
                        .getItems()
                        .stream()
                        .map(item -> item.getAmount() - item.getReservedAmount())
                        .reduce(0, Integer::sum))
                .entryDate(LocalDateTime.now())
                .customer(customer)
                .status(CartItemStatus.ACTIVE)
                .build();
        int actualReservedAmount = oldestItem.getReservedAmount();
        cart.getItem().setReservedAmount(actualReservedAmount + 1);
        cart.validate();

        return cartRepository.addItemToCart(cart);
    }

    public Map<CartItemSummaryProjection, TotalValueDTO> showCartConfirmationWithSelectedAddress(Long customerId, Long addressId) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("No customer found with id " + customerId));

        Address address = addressRepository
                .findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("No address found with id " + addressId));

        List<CartItemSummaryProjection> cartItems = cartRepository.summarizeByCustomer(customer);

        if (cartItems.isEmpty()) {
            throw new EntityNotFoundException("No cart items found for customer with id: " + customer.getId());
        }

        return cartItems
                .stream()
                .collect(Collectors
                .toMap(projection ->
                        CartItemSummaryProjection
                                .builder()
                                .customer(customer)
                                .amount(projection.getAmount())
                                .chair(projection.getChair())
                                .limit(projection.getLimit())
                                .price(projection.getPrice())
                                .build()
                        , (projection) -> {
                            double subTotal = projection.getPrice() * projection.getAmount();
                            FreightValueDTO freightValueDTO = freightCalculatorService.calculateFreight(projection.getChair(), address);
                            return new TotalValueDTO(subTotal, freightValueDTO);
                        }));

    }

    public List<CartItemSummaryProjection> findCartByCustomer(Long customerId) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("No customer found with id " + customerId));

        return cartRepository.summarizeByCustomer(customer);
    }

    public List<CartItemSummaryProjection> findCartByCustomer(User user) {
        Customer customer = customerRepository
                .findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("No customer found with user id " + user.getId()));

        return cartRepository.summarizeByCustomer(customer);
    }


    public List<Cart> findCartByCustomerAndChair(Long customerId, Long chairId) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("No customer found with user id " + customerId));

        Chair chair = chairRepository
                .findById(chairId)
                .orElseThrow(() -> new EntityNotFoundException("Chair not found with id: " + chairId));

        return cartRepository.findCartByCustomerAndChair(customer, chair);
    }

    public Map<CartItemSummaryProjection, TotalValueDTO> showCartConfirmation(Long customerId) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("No customer found with user id " + customerId));

        List<Address> addresses = addressRepository.findAllByCustomer(customer);

        if (addresses.isEmpty()) {
            throw new EntityNotFoundException("No addresses found for customer with id: " + customer.getId());
        }

        customer.setAddresses(addresses);

        List<CartItemSummaryProjection> cartItems = cartRepository.summarizeByCustomer(customer);

        if (cartItems.isEmpty()) {
            throw new EntityNotFoundException("No cart items found for customer with id: " + customer.getId());
        }

        Address defaultAddress = addresses
                .stream()
                .filter(Address::isDefault)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No default address found for customer with id: " + customer.getId()));

        return cartItems
                .stream()
                .collect(Collectors
                .toMap(projection ->
                            CartItemSummaryProjection
                                    .builder()
                                    .customer(customer)
                                    .amount(projection.getAmount())
                                    .chair(projection.getChair())
                                    .limit(projection.getLimit())
                                    .price(projection.getPrice())
                                    .build()
                    , (projection) -> {
                        double subTotal = projection.getPrice() * projection.getAmount();
                        FreightValueDTO freightValueDTO = freightCalculatorService.calculateFreight(projection.getChair(), defaultAddress);
                        return new TotalValueDTO(subTotal, freightValueDTO);
                    }));
    }

    public Double getTotalValue(Map<CartItemSummaryProjection, TotalValueDTO> totalAmountMap) {
        return totalAmountMap.values().stream()
                .mapToDouble(TotalValueDTO::totalAmount)
                .sum();
    }

    public Double getTotalFreight(Map<CartItemSummaryProjection, TotalValueDTO> totalAmountMap) {
        return totalAmountMap.values().stream()
                .map(TotalValueDTO::freight)
                .mapToDouble(FreightValueDTO::value)
                .sum();
    }

    public Double getTotalValueWithFreight(Map<CartItemSummaryProjection, TotalValueDTO> totalAmountMap) {
        return getTotalValue(totalAmountMap) + getTotalFreight(totalAmountMap);
    }

    public List<Cart> updateChairAmountInCart(Long customerId, UpdateCartItemInput input) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("No customer found with user id " + customerId));
        Chair chair = chairRepository
                .findById(input.chairId())
                .orElseThrow(() -> new EntityNotFoundException("Chair not found with id: " + input.chairId()));

        List<Cart> carts = cartRepository.findCartByCustomerAndChair(customer, chair);

        if (carts.isEmpty()) {
            throw new EntityNotFoundException("No cart found for customer with id: " + customer.getId() + " and chair with id: " + chair.getId());
        }

        int totalAmountInCart = carts
                .stream()
                .map(Cart::getAmount)
                .reduce(0, Integer::sum);

        int totalAmount = chair
                .getItems()
                .stream()
                .map(Item::getAmount)
                .reduce(0, Integer::sum);

        Integer totalReservedAmount = chair
                .getItems()
                .stream()
                .map(Item::getReservedAmount)
                .reduce(0, Integer::sum);

        int maxAmount = totalAmount - totalReservedAmount + totalAmountInCart;

        LocalDateTime entryDatetime = carts.getFirst().getEntryDate();

        if (input.amount() > maxAmount) {
            throw new AmountExceedsException("The amount exceeds the available stock");
        }

        Set<Long> itemsInCartIds = carts.stream().map(Cart::getItem)
                .map(Item::getId).collect(Collectors.toSet());

        Iterator<Cart> itemsInCart = carts
                .stream()
                .sorted(Comparator.comparing(cart -> cart.getItem().getEntryDate()))
                .toList()
                .iterator();

        Iterator<Item> itemsAvailableInStock = chair.getItems()
                .stream()
                .filter(item -> !itemsInCartIds.contains(item.getId()))
                .sorted(Comparator.comparing(Item::getEntryDate))
                .toList()
                .iterator();

        List<Cart> cartItemsToAdd = new ArrayList<>();
        List<Cart> cartItemsToUpdate = new ArrayList<>();

        int remainingAmount = input.amount();

        while (remainingAmount > 0) {
            if (!itemsInCart.hasNext() && !itemsAvailableInStock.hasNext()) {
                break;
            }

            if (itemsInCart.hasNext()) {
                Cart cart = itemsInCart.next();
                int maxAmountInStock = cart.getItem().getAmount() - cart.getItem().getReservedAmount() + cart.getAmount();
                int reservedAmount = cart.getItem().getReservedAmount();

                if (remainingAmount >= maxAmountInStock) {
                    totalAmount = cart.getItem().getAmount();

                    cart.setAmount(maxAmountInStock);
                    cart.getItem().setReservedAmount(totalAmount);
                    remainingAmount -= maxAmountInStock;
                } else {
                    int previousAmount = cart.getAmount();
                    cart.setAmount(remainingAmount);

                    int newReservedAmount = reservedAmount + (remainingAmount - previousAmount);
                    cart.getItem().setReservedAmount(newReservedAmount);
                    remainingAmount = 0;
                }
                cartItemsToUpdate.add(cart);
                continue;
            }

            if (itemsAvailableInStock.hasNext()) {
                Item item = itemsAvailableInStock.next();
                int maxAmountInStock = item.getAmount() - item.getReservedAmount();
                int reservedAmount = item.getReservedAmount();

                if (remainingAmount >= maxAmountInStock) {
                    item.setReservedAmount(maxAmountInStock);
                    totalAmount = item.getAmount();
                    cartItemsToAdd.add(Cart.builder()
                            .item(item)
                            .amount(totalAmount)
                            .price(chair.getSellPrice())
                            .limit(totalAmount)
                            .entryDate(entryDatetime)
                            .customer(customer)
                            .status(CartItemStatus.ACTIVE)
                            .build());
                    remainingAmount -= item.getAmount();
                } else {
                    item.setReservedAmount(reservedAmount + remainingAmount);
                    cartItemsToAdd.add(Cart.builder()
                            .item(item)
                            .amount(remainingAmount)
                            .price(chair.getSellPrice())
                            .limit(item.getAmount())
                            .entryDate(entryDatetime)
                            .customer(customer)
                            .status(CartItemStatus.ACTIVE)
                            .build());
                    remainingAmount = 0;
                }
            }
        }

        List<Cart> cartItemsToRemove = getItemsToRemove(itemsInCart);

        if (remainingAmount > 0) {
            throw new AmountExceedsException(String.format("The amount exceeds the available stock. Remaining amount: %d", remainingAmount));
        }

        List<Cart> cartsToPersist = new ArrayList<>();
        cartsToPersist.addAll(cartItemsToUpdate);
        cartsToPersist.addAll(cartItemsToAdd);
        cartsToPersist.addAll(cartItemsToRemove);
        cartsToPersist.forEach(Cart::validate);

        return persistAll(cartsToPersist);
    }

    private List<Cart> getItemsToRemove(Iterator<Cart> itemsInCart) {
        List<Cart> cartItemsToRemove = new ArrayList<>();

        while (itemsInCart.hasNext()) {
            Cart cart = itemsInCart.next();
            int itemReservedAmount = cart.getItem().getReservedAmount();
            int amountInCart = cart.getAmount();
            int remainingReservedAmount = itemReservedAmount - amountInCart;
            if (amountInCart > itemReservedAmount) {
                remainingReservedAmount = 0;
            }
            cart.getItem().setReservedAmount(remainingReservedAmount);
            cart.setStatus(CartItemStatus.REMOVED);
            cartItemsToRemove.add(cart);
        }
        return  cartItemsToRemove;
    }

    private List<Cart> persistAll(List<Cart> cartsToPersist) {
        Map<Long, Integer> originalVersions = cartsToPersist
                .stream()
                .map(Cart::getItem)
                .collect(Collectors.toMap(Item::getId, Item::getVersion));

        List<Cart> updatedCarts = cartRepository.updateCartItems(cartsToPersist);
        updatedCarts.stream().map(Cart::getItem).
            forEach(item -> assertStockHasUpdated(item, originalVersions.get(item.getId())));
        return updatedCarts;
    }

    private void assertStockHasUpdated(Item item, Integer beforeVersion) {
        if (beforeVersion.equals(item.getVersion())) {
            throw new OptimisticLockException("The item stock has not been updated. Item id: " + item.getId());
        }
    }

    public void deleteChairFromCart(Long customerId, Long chairId) {
        Customer customer =  customerRepository
                .findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("No customer found with user id " + customerId));
        Chair chair = chairRepository
                .findById(chairId)
                .orElseThrow(() -> new EntityNotFoundException("Chair not found with id: " + chairId));

        List<Cart> carts = cartRepository.findCartByCustomerAndChair(customer, chair);

        if (carts.isEmpty()) {
            throw new EntityNotFoundException("No cart found for customer with id: " + customer.getId() + " and chair with id: " + chairId);
        }
            carts.forEach(cart -> {
            int itemReservedAmount = cart.getItem().getReservedAmount();
            int amountInCart = cart.getAmount();
            cart.getItem().setReservedAmount(itemReservedAmount - amountInCart);
        });

        cartRepository.deleteCartItems(carts);

    }
}

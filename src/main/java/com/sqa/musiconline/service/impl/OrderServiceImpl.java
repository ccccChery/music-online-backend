package com.sqa.musiconline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqa.musiconline.config.RequestUserContext;
import com.sqa.musiconline.dto.OrderCheckoutDTO;
import com.sqa.musiconline.entity.Cart;
import com.sqa.musiconline.entity.CartItem;
import com.sqa.musiconline.entity.Order;
import com.sqa.musiconline.entity.OrderItem;
import com.sqa.musiconline.entity.User;
import com.sqa.musiconline.entity.Vinyl;
import com.sqa.musiconline.mapper.CartItemMapper;
import com.sqa.musiconline.mapper.CartMapper;
import com.sqa.musiconline.mapper.OrderItemMapper;
import com.sqa.musiconline.mapper.OrderMapper;
import com.sqa.musiconline.mapper.UserMapper;
import com.sqa.musiconline.mapper.VinylMapper;
import com.sqa.musiconline.service.OrderService;
import com.sqa.musiconline.vo.CheckoutResultVO;
import com.sqa.musiconline.vo.OrderItemVO;
import com.sqa.musiconline.vo.OrderSummaryVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal DEFAULT_SHIPPING_AMOUNT = BigDecimal.ZERO;
    private static final DateTimeFormatter ORDER_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final VinylMapper vinylMapper;
    private final UserMapper userMapper;

    public OrderServiceImpl(CartMapper cartMapper, CartItemMapper cartItemMapper, OrderMapper orderMapper,
                            OrderItemMapper orderItemMapper, VinylMapper vinylMapper, UserMapper userMapper) {
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.vinylMapper = vinylMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public CheckoutResultVO checkout(RequestUserContext.CurrentUser currentUser, OrderCheckoutDTO request) {
        Cart cart = getRequiredCart(currentUser.user().getId());
        List<CartItem> cartItems = cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getCartId, cart.getId())
                .orderByAsc(CartItem::getId));

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty.");
        }

        Map<Long, Vinyl> vinylMap = cartItems.stream()
                .map(CartItem::getVinylId)
                .distinct()
                .map(this::requireAvailableVinyl)
                .peek(vinyl -> {
                    if (currentUser.user().getId().equals(vinyl.getSellerUserId())) {
                        throw new IllegalArgumentException("You cannot place an order for your own listing.");
                    }
                })
                .collect(Collectors.toMap(Vinyl::getId, Function.identity()));

        cartItems.forEach(item -> ensureStock(vinylMap.get(item.getVinylId()), item.getQuantity()));

        Map<Long, List<CartItem>> groupedByRetailer = cartItems.stream()
                .collect(Collectors.groupingBy(item -> vinylMap.get(item.getVinylId()).getSellerUserId()));

        BigDecimal grandTotal = BigDecimal.ZERO;
        List<String> orderNumbers = new java.util.ArrayList<>();

        for (Map.Entry<Long, List<CartItem>> entry : groupedByRetailer.entrySet()) {
            Long retailerUserId = entry.getKey();
            List<CartItem> retailerItems = entry.getValue();

            BigDecimal subtotal = retailerItems.stream()
                    .map(item -> lineTotal(vinylMap.get(item.getVinylId()), item.getQuantity()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal total = subtotal.add(DEFAULT_SHIPPING_AMOUNT);

            Order order = new Order();
            order.setOrderNumber(generateOrderNumber());
            order.setBuyerUserId(currentUser.user().getId());
            order.setRetailerUserId(retailerUserId);
            order.setOrderStatus("PENDING_PAYMENT");
            order.setPaymentStatus("UNPAID");
            order.setSubtotalAmount(subtotal);
            order.setShippingAmount(DEFAULT_SHIPPING_AMOUNT);
            order.setTotalAmount(total);
            order.setShippingRecipientName(request.getShippingRecipientName().trim());
            order.setShippingPhone(request.getShippingPhone().trim());
            order.setShippingCountry(request.getShippingCountry().trim());
            order.setShippingCity(request.getShippingCity().trim());
            order.setShippingAddressLine1(request.getShippingAddressLine1().trim());
            order.setShippingAddressLine2(trimToNull(request.getShippingAddressLine2()));
            order.setShippingPostcode(request.getShippingPostcode().trim());
            order.setBuyerNote(trimToNull(request.getBuyerNote()));
            order.setOrderedAt(LocalDateTime.now());
            orderMapper.insert(order);

            for (CartItem cartItem : retailerItems) {
                Vinyl vinyl = vinylMap.get(cartItem.getVinylId());

                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setVinylId(vinyl.getId());
                orderItem.setVinylTitleSnapshot(vinyl.getTitle());
                orderItem.setArtistNameSnapshot(vinyl.getArtistName());
                orderItem.setUnitPriceSnapshot(vinyl.getPrice());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setLineTotalAmount(lineTotal(vinyl, cartItem.getQuantity()));
                orderItemMapper.insert(orderItem);

                vinyl.setStockQuantity(vinyl.getStockQuantity() - cartItem.getQuantity());
                vinyl.setListingStatus(vinyl.getStockQuantity() > 0 ? "ACTIVE" : "OUT_OF_STOCK");
                vinylMapper.updateById(vinyl);
            }

            grandTotal = grandTotal.add(total);
            orderNumbers.add(order.getOrderNumber());
        }

        cartItems.forEach(item -> cartItemMapper.deleteById(item.getId()));
        return new CheckoutResultVO(orderNumbers.size(), orderNumbers, grandTotal);
    }

    @Override
    public List<OrderSummaryVO> getBuyerOrders(RequestUserContext.CurrentUser currentUser) {
        return buildOrders(orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .eq(Order::getBuyerUserId, currentUser.user().getId())
                .orderByDesc(Order::getOrderedAt)
                .orderByDesc(Order::getId)));
    }

    @Override
    public List<OrderSummaryVO> getRetailerOrders(RequestUserContext.CurrentUser currentUser) {
        if (!currentUser.hasRole("ROLE_RETAILER")) {
            throw new IllegalArgumentException("Retailer role is required.");
        }
        return buildOrders(orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .eq(Order::getRetailerUserId, currentUser.user().getId())
                .orderByDesc(Order::getOrderedAt)
                .orderByDesc(Order::getId)));
    }

    @Override
    @Transactional
    public void confirmBuyerPayment(RequestUserContext.CurrentUser currentUser, Long orderId) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getBuyerUserId, currentUser.user().getId())
                .last("LIMIT 1"));
        if (order == null) {
            throw new IllegalArgumentException("Order not found for the current buyer.");
        }
        if (!"PENDING_PAYMENT".equals(order.getOrderStatus()) || !"UNPAID".equals(order.getPaymentStatus())) {
            throw new IllegalArgumentException("This order is not waiting for buyer payment.");
        }

        order.setPaymentStatus("PAID");
        order.setOrderStatus("PAID");
        order.setPaidAt(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void cancelBuyerOrder(RequestUserContext.CurrentUser currentUser, Long orderId) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getBuyerUserId, currentUser.user().getId())
                .last("LIMIT 1"));
        if (order == null) {
            throw new IllegalArgumentException("Order not found for the current buyer.");
        }
        if ("CANCELLED".equals(order.getOrderStatus())) {
            throw new IllegalArgumentException("This order has already been cancelled.");
        }
        if (!List.of("PENDING_PAYMENT", "PAID").contains(order.getOrderStatus())) {
            throw new IllegalArgumentException("Only unpaid or newly paid orders can be cancelled.");
        }

        List<OrderItem> orderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, order.getId())
                .orderByAsc(OrderItem::getId));
        if (orderItems.isEmpty()) {
            throw new IllegalStateException("The order has no line items to restore.");
        }

        for (OrderItem item : orderItems) {
            Vinyl vinyl = vinylMapper.selectById(item.getVinylId());
            if (vinyl == null) {
                throw new IllegalStateException("A cancelled order references a vinyl that no longer exists.");
            }

            int restoredStock = (vinyl.getStockQuantity() == null ? 0 : vinyl.getStockQuantity()) + item.getQuantity();
            vinyl.setStockQuantity(restoredStock);
            if (!"REMOVED".equals(vinyl.getListingStatus())) {
                vinyl.setListingStatus(restoredStock > 0 ? "ACTIVE" : "OUT_OF_STOCK");
            }
            vinylMapper.updateById(vinyl);
        }

        order.setOrderStatus("CANCELLED");
        order.setCancelledAt(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void updateRetailerOrderStatus(RequestUserContext.CurrentUser currentUser, Long orderId, String orderStatus) {
        if (!currentUser.hasRole("ROLE_RETAILER")) {
            throw new IllegalArgumentException("Retailer role is required.");
        }

        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getRetailerUserId, currentUser.user().getId())
                .last("LIMIT 1"));
        if (order == null) {
            throw new IllegalArgumentException("Order not found for the current retailer.");
        }

        String nextStatus = orderStatus.trim().toUpperCase();
        if (!List.of("PROCESSING", "SHIPPED", "COMPLETED").contains(nextStatus)) {
            throw new IllegalArgumentException("Retailer orderStatus must be PROCESSING, SHIPPED, or COMPLETED.");
        }

        String currentStatus = order.getOrderStatus();
        boolean validTransition = ("PAID".equals(currentStatus) && "PROCESSING".equals(nextStatus))
                || ("PROCESSING".equals(currentStatus) && "SHIPPED".equals(nextStatus))
                || ("SHIPPED".equals(currentStatus) && "COMPLETED".equals(nextStatus));
        if (!validTransition) {
            throw new IllegalArgumentException("The requested order status transition is not allowed.");
        }
        if (!"PAID".equals(order.getPaymentStatus())) {
            throw new IllegalArgumentException("Only paid orders can be processed by the retailer.");
        }

        order.setOrderStatus(nextStatus);
        if ("COMPLETED".equals(nextStatus)) {
            order.setFulfilledAt(LocalDateTime.now());
        }
        orderMapper.updateById(order);
    }

    private List<OrderSummaryVO> buildOrders(List<Order> orders) {
        if (orders.isEmpty()) {
            return List.of();
        }

        Map<Long, User> userMap = orders.stream()
                .flatMap(order -> java.util.stream.Stream.of(order.getBuyerUserId(), order.getRetailerUserId()))
                .distinct()
                .map(userMapper::selectById)
                .filter(user -> user != null)
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return orders.stream()
                .map(order -> {
                    List<OrderItemVO> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                                    .eq(OrderItem::getOrderId, order.getId())
                                    .orderByAsc(OrderItem::getId))
                            .stream()
                            .map(item -> new OrderItemVO(
                                    item.getId(),
                                    item.getVinylId(),
                                    item.getVinylTitleSnapshot(),
                                    item.getArtistNameSnapshot(),
                                    item.getUnitPriceSnapshot(),
                                    item.getQuantity(),
                                    item.getLineTotalAmount()
                            ))
                            .toList();

                    User buyer = userMap.get(order.getBuyerUserId());
                    User retailer = userMap.get(order.getRetailerUserId());
                    return new OrderSummaryVO(
                            order.getId(),
                            order.getOrderNumber(),
                            buyer != null ? buyer.getDisplayName() : "Unknown buyer",
                            retailer != null ? retailer.getDisplayName() : "Unknown retailer",
                            order.getOrderStatus(),
                            order.getPaymentStatus(),
                            order.getSubtotalAmount(),
                            order.getShippingAmount(),
                            order.getTotalAmount(),
                            order.getShippingRecipientName(),
                            order.getShippingPhone(),
                            order.getShippingCountry(),
                            order.getShippingCity(),
                            order.getShippingAddressLine1(),
                            order.getShippingAddressLine2(),
                            order.getShippingPostcode(),
                            order.getBuyerNote(),
                            order.getOrderedAt(),
                            items
                    );
                })
                .toList();
    }

    private Cart getRequiredCart(Long userId) {
        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .last("LIMIT 1"));
        if (cart == null) {
            throw new IllegalArgumentException("Your cart is empty.");
        }
        return cart;
    }

    private Vinyl requireAvailableVinyl(Long vinylId) {
        Vinyl vinyl = vinylMapper.selectById(vinylId);
        if (vinyl == null) {
            throw new IllegalArgumentException("A cart item references a vinyl that no longer exists.");
        }
        if (!"ACTIVE".equals(vinyl.getListingStatus()) || vinyl.getStockQuantity() == null || vinyl.getStockQuantity() < 1) {
            throw new IllegalArgumentException("One or more cart items are no longer available.");
        }
        return vinyl;
    }

    private void ensureStock(Vinyl vinyl, Integer quantity) {
        if (vinyl.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("One or more cart items exceed the current stock.");
        }
    }

    private BigDecimal lineTotal(Vinyl vinyl, Integer quantity) {
        return vinyl.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(ORDER_NUMBER_FORMAT);
        int randomSuffix = ThreadLocalRandom.current().nextInt(100, 1000);
        return "MO" + timestamp + randomSuffix;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}

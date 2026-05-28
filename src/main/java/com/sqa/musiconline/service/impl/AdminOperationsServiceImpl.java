package com.sqa.musiconline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqa.musiconline.config.RequestAdminContext;
import com.sqa.musiconline.entity.Order;
import com.sqa.musiconline.entity.OrderItem;
import com.sqa.musiconline.entity.User;
import com.sqa.musiconline.entity.Vinyl;
import com.sqa.musiconline.mapper.OrderItemMapper;
import com.sqa.musiconline.mapper.OrderMapper;
import com.sqa.musiconline.mapper.UserMapper;
import com.sqa.musiconline.mapper.VinylMapper;
import com.sqa.musiconline.service.AdminOperationsService;
import com.sqa.musiconline.vo.AdminVinylOverviewVO;
import com.sqa.musiconline.vo.OrderItemVO;
import com.sqa.musiconline.vo.OrderSummaryVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminOperationsServiceImpl implements AdminOperationsService {

    private final VinylMapper vinylMapper;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public AdminOperationsServiceImpl(VinylMapper vinylMapper, UserMapper userMapper, OrderMapper orderMapper,
                                      OrderItemMapper orderItemMapper) {
        this.vinylMapper = vinylMapper;
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public List<AdminVinylOverviewVO> listVinyls(RequestAdminContext.CurrentAdmin currentAdmin) {
        List<Vinyl> vinyls = vinylMapper.selectList(new LambdaQueryWrapper<Vinyl>()
                .orderByDesc(Vinyl::getUpdatedAt)
                .orderByDesc(Vinyl::getId));
        if (vinyls.isEmpty()) {
            return List.of();
        }

        Map<Long, User> sellerMap = vinyls.stream()
                .map(Vinyl::getSellerUserId)
                .filter(id -> id != null)
                .distinct()
                .map(userMapper::selectById)
                .filter(user -> user != null)
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return vinyls.stream()
                .map(vinyl -> {
                    User seller = sellerMap.get(vinyl.getSellerUserId());
                    return new AdminVinylOverviewVO(
                            vinyl.getId(),
                            vinyl.getSellerUserId(),
                            seller != null ? seller.getDisplayName() : "Unknown retailer",
                            vinyl.getArtistName(),
                            vinyl.getTitle(),
                            vinyl.getFormatType(),
                            vinyl.getGenreName(),
                            vinyl.getConditionGrade(),
                            vinyl.getReleaseDate(),
                            vinyl.getPrice(),
                            vinyl.getStockQuantity(),
                            vinyl.getListingStatus(),
                            vinyl.getDescription(),
                            vinyl.getCoverImageUrl(),
                            vinyl.getCreatedAt(),
                            vinyl.getUpdatedAt()
                    );
                })
                .toList();
    }

    @Override
    public List<OrderSummaryVO> listOrders(RequestAdminContext.CurrentAdmin currentAdmin) {
        List<Order> orders = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .orderByDesc(Order::getOrderedAt)
                .orderByDesc(Order::getId));
        if (orders.isEmpty()) {
            return List.of();
        }

        Map<Long, User> userMap = orders.stream()
                .flatMap(order -> java.util.stream.Stream.of(order.getBuyerUserId(), order.getRetailerUserId()))
                .filter(id -> id != null)
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
}

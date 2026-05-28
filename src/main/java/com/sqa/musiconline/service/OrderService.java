package com.sqa.musiconline.service;

import com.sqa.musiconline.config.RequestUserContext;
import com.sqa.musiconline.dto.OrderCheckoutDTO;
import com.sqa.musiconline.vo.CheckoutResultVO;
import com.sqa.musiconline.vo.OrderSummaryVO;

import java.util.List;

public interface OrderService {

    CheckoutResultVO checkout(RequestUserContext.CurrentUser currentUser, OrderCheckoutDTO request);

    List<OrderSummaryVO> getBuyerOrders(RequestUserContext.CurrentUser currentUser);

    List<OrderSummaryVO> getRetailerOrders(RequestUserContext.CurrentUser currentUser);

    void confirmBuyerPayment(RequestUserContext.CurrentUser currentUser, Long orderId);

    void cancelBuyerOrder(RequestUserContext.CurrentUser currentUser, Long orderId);

    void updateRetailerOrderStatus(RequestUserContext.CurrentUser currentUser, Long orderId, String orderStatus);
}

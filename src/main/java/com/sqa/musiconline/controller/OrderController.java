package com.sqa.musiconline.controller;

import com.sqa.musiconline.common.ApiResponse;
import com.sqa.musiconline.config.RequestUserContext;
import com.sqa.musiconline.dto.OrderCheckoutDTO;
import com.sqa.musiconline.dto.OrderStatusUpdateDTO;
import com.sqa.musiconline.service.OrderService;
import com.sqa.musiconline.vo.CheckoutResultVO;
import com.sqa.musiconline.vo.OrderSummaryVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final RequestUserContext requestUserContext;
    private final OrderService orderService;

    public OrderController(RequestUserContext requestUserContext, OrderService orderService) {
        this.requestUserContext = requestUserContext;
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ApiResponse<CheckoutResultVO> checkout(@RequestHeader("X-User-Id") String userIdHeader,
                                                  @Valid @RequestBody OrderCheckoutDTO request) {
        return ApiResponse.ok(orderService.checkout(requestUserContext.requireUser(userIdHeader), request));
    }

    @GetMapping("/mine")
    public ApiResponse<List<OrderSummaryVO>> mine(@RequestHeader("X-User-Id") String userIdHeader) {
        return ApiResponse.ok(orderService.getBuyerOrders(requestUserContext.requireUser(userIdHeader)));
    }

    @PostMapping("/{id}/confirm-payment")
    public ApiResponse<Void> confirmPayment(@RequestHeader("X-User-Id") String userIdHeader,
                                            @PathVariable("id") Long id) {
        orderService.confirmBuyerPayment(requestUserContext.requireUser(userIdHeader), id);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@RequestHeader("X-User-Id") String userIdHeader,
                                    @PathVariable("id") Long id) {
        orderService.cancelBuyerOrder(requestUserContext.requireUser(userIdHeader), id);
        return ApiResponse.ok();
    }
}

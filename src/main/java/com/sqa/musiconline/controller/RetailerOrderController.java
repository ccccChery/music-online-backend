package com.sqa.musiconline.controller;

import com.sqa.musiconline.common.ApiResponse;
import com.sqa.musiconline.config.RequestUserContext;
import com.sqa.musiconline.dto.OrderStatusUpdateDTO;
import com.sqa.musiconline.service.OrderService;
import com.sqa.musiconline.vo.OrderSummaryVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/retailer/orders")
public class RetailerOrderController {

    private final RequestUserContext requestUserContext;
    private final OrderService orderService;

    public RetailerOrderController(RequestUserContext requestUserContext, OrderService orderService) {
        this.requestUserContext = requestUserContext;
        this.orderService = orderService;
    }

    @GetMapping("/mine")
    public ApiResponse<List<OrderSummaryVO>> mine(@RequestHeader("X-User-Id") String userIdHeader) {
        return ApiResponse.ok(orderService.getRetailerOrders(requestUserContext.requireUser(userIdHeader)));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@RequestHeader("X-User-Id") String userIdHeader,
                                          @PathVariable("id") Long id,
                                          @Valid @RequestBody OrderStatusUpdateDTO request) {
        orderService.updateRetailerOrderStatus(requestUserContext.requireUser(userIdHeader), id, request.getOrderStatus());
        return ApiResponse.ok();
    }
}

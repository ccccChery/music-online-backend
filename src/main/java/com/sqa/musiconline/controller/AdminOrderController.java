package com.sqa.musiconline.controller;

import com.sqa.musiconline.common.ApiResponse;
import com.sqa.musiconline.config.RequestAdminContext;
import com.sqa.musiconline.service.AdminOperationsService;
import com.sqa.musiconline.vo.OrderSummaryVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final RequestAdminContext requestAdminContext;
    private final AdminOperationsService adminOperationsService;

    public AdminOrderController(RequestAdminContext requestAdminContext, AdminOperationsService adminOperationsService) {
        this.requestAdminContext = requestAdminContext;
        this.adminOperationsService = adminOperationsService;
    }

    @GetMapping
    public ApiResponse<List<OrderSummaryVO>> list(@RequestHeader("X-Admin-Id") String adminIdHeader) {
        return ApiResponse.ok(adminOperationsService.listOrders(requestAdminContext.requireAdmin(adminIdHeader)));
    }
}

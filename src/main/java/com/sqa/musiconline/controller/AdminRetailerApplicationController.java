package com.sqa.musiconline.controller;

import com.sqa.musiconline.common.ApiResponse;
import com.sqa.musiconline.config.RequestAdminContext;
import com.sqa.musiconline.dto.AdminRetailerApplicationReviewDTO;
import com.sqa.musiconline.service.AdminRetailerApplicationService;
import com.sqa.musiconline.vo.AdminRetailerApplicationVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/retailer-applications")
public class AdminRetailerApplicationController {

    private final RequestAdminContext requestAdminContext;
    private final AdminRetailerApplicationService adminRetailerApplicationService;

    public AdminRetailerApplicationController(RequestAdminContext requestAdminContext,
                                              AdminRetailerApplicationService adminRetailerApplicationService) {
        this.requestAdminContext = requestAdminContext;
        this.adminRetailerApplicationService = adminRetailerApplicationService;
    }

    @GetMapping
    public ApiResponse<List<AdminRetailerApplicationVO>> list(@RequestHeader("X-Admin-Id") String adminIdHeader) {
        return ApiResponse.ok(adminRetailerApplicationService.listApplications(requestAdminContext.requireAdmin(adminIdHeader)));
    }

    @PutMapping("/{id}/review")
    public ApiResponse<Void> review(@RequestHeader("X-Admin-Id") String adminIdHeader,
                                    @PathVariable("id") Long id,
                                    @Valid @RequestBody AdminRetailerApplicationReviewDTO request) {
        adminRetailerApplicationService.reviewApplication(requestAdminContext.requireAdmin(adminIdHeader), id, request);
        return ApiResponse.ok();
    }
}

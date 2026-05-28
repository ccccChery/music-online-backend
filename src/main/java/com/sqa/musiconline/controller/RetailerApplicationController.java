package com.sqa.musiconline.controller;

import com.sqa.musiconline.common.ApiResponse;
import com.sqa.musiconline.config.RequestUserContext;
import com.sqa.musiconline.dto.RetailerApplicationCreateDTO;
import com.sqa.musiconline.service.RetailerApplicationService;
import com.sqa.musiconline.vo.RetailerApplicationStatusVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/retailer-applications")
public class RetailerApplicationController {

    private final RequestUserContext requestUserContext;
    private final RetailerApplicationService retailerApplicationService;

    public RetailerApplicationController(RequestUserContext requestUserContext,
                                         RetailerApplicationService retailerApplicationService) {
        this.requestUserContext = requestUserContext;
        this.retailerApplicationService = retailerApplicationService;
    }

    @PostMapping
    public ApiResponse<Void> submit(@RequestHeader("X-User-Id") String userIdHeader,
                                    @Valid @RequestBody RetailerApplicationCreateDTO request) {
        retailerApplicationService.submitApplication(requestUserContext.requireUser(userIdHeader), request);
        return ApiResponse.ok();
    }

    @GetMapping("/latest")
    public ApiResponse<RetailerApplicationStatusVO> latest(@RequestHeader("X-User-Id") String userIdHeader) {
        return ApiResponse.ok(retailerApplicationService.getLatestApplication(requestUserContext.requireUser(userIdHeader)));
    }

    @PostMapping("/{id}/confirm-payment")
    public ApiResponse<Void> confirmPayment(@RequestHeader("X-User-Id") String userIdHeader,
                                            @PathVariable("id") Long id) {
        retailerApplicationService.confirmApplicationFeePayment(requestUserContext.requireUser(userIdHeader), id);
        return ApiResponse.ok();
    }
}

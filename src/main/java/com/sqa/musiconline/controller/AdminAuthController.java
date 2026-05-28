package com.sqa.musiconline.controller;

import com.sqa.musiconline.common.ApiResponse;
import com.sqa.musiconline.dto.AdminLoginRequestDTO;
import com.sqa.musiconline.service.AdminAuthService;
import com.sqa.musiconline.vo.AdminLoginResponseVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/login")
    public ApiResponse<AdminLoginResponseVO> login(@Valid @RequestBody AdminLoginRequestDTO request) {
        return ApiResponse.ok(adminAuthService.login(request));
    }
}

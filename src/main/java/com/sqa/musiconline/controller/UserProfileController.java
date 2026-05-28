package com.sqa.musiconline.controller;

import com.sqa.musiconline.common.ApiResponse;
import com.sqa.musiconline.config.RequestUserContext;
import com.sqa.musiconline.dto.UserProfileUpdateDTO;
import com.sqa.musiconline.service.UserProfileService;
import com.sqa.musiconline.vo.UserProfileVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {

    private final RequestUserContext requestUserContext;
    private final UserProfileService userProfileService;

    public UserProfileController(RequestUserContext requestUserContext, UserProfileService userProfileService) {
        this.requestUserContext = requestUserContext;
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ApiResponse<UserProfileVO> profile(@RequestHeader("X-User-Id") String userIdHeader) {
        return ApiResponse.ok(userProfileService.getProfile(requestUserContext.requireUser(userIdHeader)));
    }

    @PutMapping
    public ApiResponse<UserProfileVO> update(@RequestHeader("X-User-Id") String userIdHeader,
                                             @Valid @RequestBody UserProfileUpdateDTO request) {
        return ApiResponse.ok(userProfileService.updateProfile(requestUserContext.requireUser(userIdHeader), request));
    }
}

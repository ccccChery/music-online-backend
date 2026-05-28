package com.sqa.musiconline.controller;

import com.sqa.musiconline.common.ApiResponse;
import com.sqa.musiconline.config.RequestUserContext;
import com.sqa.musiconline.dto.RetailerVinylSaveDTO;
import com.sqa.musiconline.service.VinylService;
import com.sqa.musiconline.vo.RetailerVinylManageVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/retailer/vinyls")
public class RetailerVinylController {

    private final RequestUserContext requestUserContext;
    private final VinylService vinylService;

    public RetailerVinylController(RequestUserContext requestUserContext, VinylService vinylService) {
        this.requestUserContext = requestUserContext;
        this.vinylService = vinylService;
    }

    @GetMapping("/mine")
    public ApiResponse<List<RetailerVinylManageVO>> mine(@RequestHeader("X-User-Id") String userIdHeader) {
        return ApiResponse.ok(vinylService.getRetailerVinyls(requestUserContext.requireUser(userIdHeader)));
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestHeader("X-User-Id") String userIdHeader,
                                    @Valid @RequestBody RetailerVinylSaveDTO request) {
        vinylService.createRetailerVinyl(requestUserContext.requireUser(userIdHeader), request);
        return ApiResponse.ok();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@RequestHeader("X-User-Id") String userIdHeader,
                                    @PathVariable("id") Long id,
                                    @Valid @RequestBody RetailerVinylSaveDTO request) {
        vinylService.updateRetailerVinyl(requestUserContext.requireUser(userIdHeader), id, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@RequestHeader("X-User-Id") String userIdHeader,
                                    @PathVariable("id") Long id) {
        vinylService.deleteRetailerVinyl(requestUserContext.requireUser(userIdHeader), id);
        return ApiResponse.ok();
    }
}

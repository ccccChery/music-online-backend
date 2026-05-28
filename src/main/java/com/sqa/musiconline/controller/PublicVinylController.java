package com.sqa.musiconline.controller;

import com.sqa.musiconline.common.ApiResponse;
import com.sqa.musiconline.dto.VinylSearchRequestDTO;
import com.sqa.musiconline.service.VinylService;
import com.sqa.musiconline.vo.VinylCardVO;
import com.sqa.musiconline.vo.VinylDetailVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/vinyls")
public class PublicVinylController {

    private final VinylService vinylService;

    public PublicVinylController(VinylService vinylService) {
        this.vinylService = vinylService;
    }

    @GetMapping("/search")
    public ApiResponse<List<VinylCardVO>> search(VinylSearchRequestDTO request) {
        return ApiResponse.ok(vinylService.searchPublicVinyls(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<VinylDetailVO> detail(@PathVariable("id") Long id) {
        return ApiResponse.ok(vinylService.getPublicVinylDetail(id));
    }
}

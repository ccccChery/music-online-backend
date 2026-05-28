package com.sqa.musiconline.service;

import com.sqa.musiconline.config.RequestUserContext;
import com.sqa.musiconline.dto.RetailerVinylSaveDTO;
import com.sqa.musiconline.dto.VinylSearchRequestDTO;
import com.sqa.musiconline.vo.RetailerVinylManageVO;
import com.sqa.musiconline.vo.VinylCardVO;
import com.sqa.musiconline.vo.VinylDetailVO;

import java.util.List;

public interface VinylService {

    List<VinylCardVO> searchPublicVinyls(VinylSearchRequestDTO request);

    VinylDetailVO getPublicVinylDetail(Long vinylId);

    List<RetailerVinylManageVO> getRetailerVinyls(RequestUserContext.CurrentUser currentUser);

    void createRetailerVinyl(RequestUserContext.CurrentUser currentUser, RetailerVinylSaveDTO request);

    void updateRetailerVinyl(RequestUserContext.CurrentUser currentUser, Long vinylId, RetailerVinylSaveDTO request);

    void deleteRetailerVinyl(RequestUserContext.CurrentUser currentUser, Long vinylId);
}

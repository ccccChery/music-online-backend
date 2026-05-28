package com.sqa.musiconline.service;

import com.sqa.musiconline.config.RequestAdminContext;
import com.sqa.musiconline.dto.AdminRetailerApplicationReviewDTO;
import com.sqa.musiconline.vo.AdminRetailerApplicationVO;

import java.util.List;

public interface AdminRetailerApplicationService {

    List<AdminRetailerApplicationVO> listApplications(RequestAdminContext.CurrentAdmin currentAdmin);

    void reviewApplication(RequestAdminContext.CurrentAdmin currentAdmin, Long applicationId, AdminRetailerApplicationReviewDTO request);
}

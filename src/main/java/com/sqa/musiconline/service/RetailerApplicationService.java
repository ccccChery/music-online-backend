package com.sqa.musiconline.service;

import com.sqa.musiconline.config.RequestUserContext;
import com.sqa.musiconline.dto.RetailerApplicationCreateDTO;
import com.sqa.musiconline.vo.RetailerApplicationStatusVO;

public interface RetailerApplicationService {

    void submitApplication(RequestUserContext.CurrentUser currentUser, RetailerApplicationCreateDTO request);

    RetailerApplicationStatusVO getLatestApplication(RequestUserContext.CurrentUser currentUser);

    void confirmApplicationFeePayment(RequestUserContext.CurrentUser currentUser, Long applicationId);
}

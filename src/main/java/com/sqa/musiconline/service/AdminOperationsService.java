package com.sqa.musiconline.service;

import com.sqa.musiconline.config.RequestAdminContext;
import com.sqa.musiconline.vo.AdminVinylOverviewVO;
import com.sqa.musiconline.vo.OrderSummaryVO;

import java.util.List;

public interface AdminOperationsService {

    List<AdminVinylOverviewVO> listVinyls(RequestAdminContext.CurrentAdmin currentAdmin);

    List<OrderSummaryVO> listOrders(RequestAdminContext.CurrentAdmin currentAdmin);
}

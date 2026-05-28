package com.sqa.musiconline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqa.musiconline.config.RequestUserContext;
import com.sqa.musiconline.dto.RetailerApplicationCreateDTO;
import com.sqa.musiconline.entity.RetailerApplication;
import com.sqa.musiconline.mapper.RetailerApplicationMapper;
import com.sqa.musiconline.service.RetailerApplicationService;
import com.sqa.musiconline.vo.RetailerApplicationStatusVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RetailerApplicationServiceImpl implements RetailerApplicationService {

    private static final BigDecimal FIXED_RETAILER_FEE = new BigDecimal("49.99");

    private final RetailerApplicationMapper retailerApplicationMapper;

    public RetailerApplicationServiceImpl(RetailerApplicationMapper retailerApplicationMapper) {
        this.retailerApplicationMapper = retailerApplicationMapper;
    }

    @Override
    @Transactional
    public void submitApplication(RequestUserContext.CurrentUser currentUser, RetailerApplicationCreateDTO request) {
        if (currentUser.hasRole("ROLE_RETAILER")) {
            throw new IllegalArgumentException("Retailer users cannot apply again.");
        }

        RetailerApplication existingPending = retailerApplicationMapper.selectOne(new LambdaQueryWrapper<RetailerApplication>()
                .eq(RetailerApplication::getUserId, currentUser.user().getId())
                .eq(RetailerApplication::getReviewStatus, "PENDING")
                .orderByDesc(RetailerApplication::getId)
                .last("LIMIT 1"));
        if (existingPending != null) {
            throw new IllegalArgumentException("You already have a pending retailer application.");
        }

        RetailerApplication application = new RetailerApplication();
        application.setUserId(currentUser.user().getId());
        application.setProposedStoreName(request.getProposedStoreName().trim());
        application.setBusinessRegion(trimToNull(request.getBusinessRegion()));
        application.setEstimatedInventoryCount(request.getEstimatedInventoryCount());
        application.setApplicationMessage(trimToNull(request.getApplicationMessage()));
        application.setFeeAmountSnapshot(FIXED_RETAILER_FEE);
        application.setFeeStatus("UNPAID");
        application.setReviewStatus("PENDING");
        retailerApplicationMapper.insert(application);
    }

    @Override
    public RetailerApplicationStatusVO getLatestApplication(RequestUserContext.CurrentUser currentUser) {
        RetailerApplication application = retailerApplicationMapper.selectOne(new LambdaQueryWrapper<RetailerApplication>()
                .eq(RetailerApplication::getUserId, currentUser.user().getId())
                .orderByDesc(RetailerApplication::getId)
                .last("LIMIT 1"));
        if (application == null) {
            return null;
        }
        return new RetailerApplicationStatusVO(
                application.getId(),
                application.getProposedStoreName(),
                application.getBusinessRegion(),
                application.getEstimatedInventoryCount(),
                application.getApplicationMessage(),
                application.getFeeAmountSnapshot(),
                application.getFeeStatus(),
                application.getReviewStatus(),
                application.getReviewNote(),
                application.getSubmittedAt(),
                application.getReviewedAt(),
                isFeePaymentAllowed(application)
        );
    }

    @Override
    @Transactional
    public void confirmApplicationFeePayment(RequestUserContext.CurrentUser currentUser, Long applicationId) {
        RetailerApplication application = retailerApplicationMapper.selectOne(new LambdaQueryWrapper<RetailerApplication>()
                .eq(RetailerApplication::getId, applicationId)
                .eq(RetailerApplication::getUserId, currentUser.user().getId())
                .last("LIMIT 1"));
        if (application == null) {
            throw new IllegalArgumentException("Retailer application not found.");
        }
        if (!isFeePaymentAllowed(application)) {
            throw new IllegalArgumentException("This retailer application cannot be paid at its current status.");
        }

        application.setFeeAmountSnapshot(FIXED_RETAILER_FEE);
        application.setFeeStatus("PAID");
        retailerApplicationMapper.updateById(application);
    }

    public static BigDecimal getFixedRetailerFee() {
        return FIXED_RETAILER_FEE;
    }

    private boolean isFeePaymentAllowed(RetailerApplication application) {
        return "PENDING".equals(application.getReviewStatus()) && "UNPAID".equals(application.getFeeStatus());
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}

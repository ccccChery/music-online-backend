package com.sqa.musiconline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqa.musiconline.config.RequestAdminContext;
import com.sqa.musiconline.dto.AdminRetailerApplicationReviewDTO;
import com.sqa.musiconline.entity.Admin;
import com.sqa.musiconline.entity.RetailerApplication;
import com.sqa.musiconline.entity.Role;
import com.sqa.musiconline.entity.User;
import com.sqa.musiconline.entity.UserRole;
import com.sqa.musiconline.mapper.AdminMapper;
import com.sqa.musiconline.mapper.RetailerApplicationMapper;
import com.sqa.musiconline.mapper.RoleMapper;
import com.sqa.musiconline.mapper.UserMapper;
import com.sqa.musiconline.mapper.UserRoleMapper;
import com.sqa.musiconline.service.AdminRetailerApplicationService;
import com.sqa.musiconline.vo.AdminRetailerApplicationVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminRetailerApplicationServiceImpl implements AdminRetailerApplicationService {

    private final RetailerApplicationMapper retailerApplicationMapper;
    private final UserMapper userMapper;
    private final AdminMapper adminMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    public AdminRetailerApplicationServiceImpl(RetailerApplicationMapper retailerApplicationMapper, UserMapper userMapper,
                                               AdminMapper adminMapper, RoleMapper roleMapper, UserRoleMapper userRoleMapper) {
        this.retailerApplicationMapper = retailerApplicationMapper;
        this.userMapper = userMapper;
        this.adminMapper = adminMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public List<AdminRetailerApplicationVO> listApplications(RequestAdminContext.CurrentAdmin currentAdmin) {
        List<RetailerApplication> applications = retailerApplicationMapper.selectList(new LambdaQueryWrapper<RetailerApplication>()
                .orderByDesc(RetailerApplication::getId));

        if (applications.isEmpty()) {
            return List.of();
        }

        applications = applications.stream()
                .sorted((left, right) -> {
                    int leftPriority = reviewPriority(left.getReviewStatus());
                    int rightPriority = reviewPriority(right.getReviewStatus());
                    if (leftPriority != rightPriority) {
                        return Integer.compare(leftPriority, rightPriority);
                    }
                    return Long.compare(right.getId(), left.getId());
                })
                .toList();

        Map<Long, User> userMap = applications.stream()
                .map(RetailerApplication::getUserId)
                .distinct()
                .map(userMapper::selectById)
                .filter(user -> user != null)
                .collect(Collectors.toMap(User::getId, Function.identity()));

        Map<Long, Admin> adminMap = applications.stream()
                .map(RetailerApplication::getReviewedByAdminId)
                .filter(id -> id != null)
                .distinct()
                .map(adminMapper::selectById)
                .filter(admin -> admin != null)
                .collect(Collectors.toMap(Admin::getId, Function.identity()));

        return applications.stream()
                .map(application -> {
                    User user = userMap.get(application.getUserId());
                    Admin admin = application.getReviewedByAdminId() == null ? null : adminMap.get(application.getReviewedByAdminId());
                    return new AdminRetailerApplicationVO(
                            application.getId(),
                            application.getUserId(),
                            user != null ? user.getUsername() : "Unknown user",
                            user != null ? user.getDisplayName() : "Unknown user",
                            user != null ? user.getEmail() : null,
                            user != null ? user.getPhone() : null,
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
                            application.getReviewedByAdminId(),
                            admin != null ? admin.getDisplayName() : null
                    );
                })
                .toList();
    }

    @Override
    @Transactional
    public void reviewApplication(RequestAdminContext.CurrentAdmin currentAdmin, Long applicationId, AdminRetailerApplicationReviewDTO request) {
        RetailerApplication application = retailerApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new IllegalArgumentException("Retailer application not found.");
        }
        if (!"PENDING".equals(application.getReviewStatus())) {
            throw new IllegalArgumentException("Only pending applications can be reviewed.");
        }

        String reviewStatus = request.getReviewStatus().trim().toUpperCase();
        if (!List.of("APPROVED", "REJECTED").contains(reviewStatus)) {
            throw new IllegalArgumentException("reviewStatus must be APPROVED or REJECTED.");
        }

        String nextFeeStatus = StringUtils.hasText(request.getFeeStatus())
                ? request.getFeeStatus().trim().toUpperCase()
                : application.getFeeStatus();
        if (!List.of("UNPAID", "PAID", "WAIVED").contains(nextFeeStatus)) {
            throw new IllegalArgumentException("feeStatus must be UNPAID, PAID, or WAIVED.");
        }
        if ("APPROVED".equals(reviewStatus) && "UNPAID".equals(nextFeeStatus)) {
            throw new IllegalArgumentException("Approved applications must have feeStatus PAID or WAIVED.");
        }

        application.setFeeStatus(nextFeeStatus);
        application.setReviewStatus(reviewStatus);
        application.setReviewNote(trimToNull(request.getReviewNote()));
        application.setReviewedAt(LocalDateTime.now());
        application.setReviewedByAdminId(currentAdmin.admin().getId());
        retailerApplicationMapper.updateById(application);

        if ("APPROVED".equals(reviewStatus)) {
            grantRetailerRole(application.getUserId());
        }
    }

    private void grantRetailerRole(Long userId) {
        Role retailerRole = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleKey, "ROLE_RETAILER")
                .last("LIMIT 1"));
        if (retailerRole == null) {
            throw new IllegalStateException("ROLE_RETAILER is missing in roles table.");
        }

        UserRole existingAssignment = userRoleMapper.selectOne(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
                .eq(UserRole::getRoleId, retailerRole.getId())
                .last("LIMIT 1"));
        if (existingAssignment != null) {
            return;
        }

        UserRole assignment = new UserRole();
        assignment.setUserId(userId);
        assignment.setRoleId(retailerRole.getId());
        userRoleMapper.insert(assignment);
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private int reviewPriority(String reviewStatus) {
        if ("PENDING".equals(reviewStatus)) {
            return 0;
        }
        if ("REJECTED".equals(reviewStatus)) {
            return 1;
        }
        if ("APPROVED".equals(reviewStatus)) {
            return 2;
        }
        return 3;
    }
}

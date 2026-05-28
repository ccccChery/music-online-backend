package com.sqa.musiconline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqa.musiconline.dto.AdminLoginRequestDTO;
import com.sqa.musiconline.entity.Admin;
import com.sqa.musiconline.mapper.AdminMapper;
import com.sqa.musiconline.service.AdminAuthService;
import com.sqa.musiconline.utils.PasswordUtil;
import com.sqa.musiconline.vo.AdminLoginResponseVO;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminMapper adminMapper;

    public AdminAuthServiceImpl(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    @Override
    public AdminLoginResponseVO login(AdminLoginRequestDTO request) {
        String loginAccount = request.getLoginAccount().trim();
        Admin admin = loginAccount.contains("@")
                ? adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getEmail, loginAccount.toLowerCase())
                .last("LIMIT 1"))
                : adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getAdminUsername, loginAccount)
                .last("LIMIT 1"));

        if (admin == null) {
            throw new IllegalArgumentException("Admin account does not exist.");
        }
        if (!"ACTIVE".equals(admin.getAccountStatus())) {
            throw new IllegalArgumentException("This admin account is not active.");
        }

        String hashedPassword = PasswordUtil.hashPassword(request.getPassword());
        if (!hashedPassword.equals(admin.getPasswordHash())) {
            throw new IllegalArgumentException("Password is incorrect.");
        }

        return new AdminLoginResponseVO(
                admin.getId(),
                admin.getAdminUsername(),
                admin.getDisplayName(),
                admin.getAccountStatus(),
                "ROLE_ADMIN"
        );
    }
}

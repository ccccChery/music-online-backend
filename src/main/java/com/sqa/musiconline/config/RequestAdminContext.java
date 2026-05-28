package com.sqa.musiconline.config;

import com.sqa.musiconline.entity.Admin;
import com.sqa.musiconline.mapper.AdminMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RequestAdminContext {

    private final AdminMapper adminMapper;

    public RequestAdminContext(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    public CurrentAdmin requireAdmin(String adminIdHeader) {
        if (!StringUtils.hasText(adminIdHeader)) {
            throw new IllegalArgumentException("X-Admin-Id header is required.");
        }

        Long adminId;
        try {
            adminId = Long.valueOf(adminIdHeader.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("X-Admin-Id must be a valid numeric admin id.");
        }

        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new IllegalArgumentException("Current admin does not exist.");
        }
        if (!"ACTIVE".equals(admin.getAccountStatus())) {
            throw new IllegalArgumentException("Current admin is not active.");
        }

        return new CurrentAdmin(admin);
    }

    public record CurrentAdmin(Admin admin) {
    }
}

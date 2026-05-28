package com.sqa.musiconline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqa.musiconline.dto.LoginRequestDTO;
import com.sqa.musiconline.dto.RegisterRequestDTO;
import com.sqa.musiconline.entity.Role;
import com.sqa.musiconline.entity.User;
import com.sqa.musiconline.entity.UserRole;
import com.sqa.musiconline.mapper.RoleMapper;
import com.sqa.musiconline.mapper.UserRoleMapper;
import com.sqa.musiconline.service.AuthService;
import com.sqa.musiconline.service.UserService;
import com.sqa.musiconline.utils.PasswordUtil;
import com.sqa.musiconline.vo.LoginResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    public AuthServiceImpl(UserService userService, UserRoleMapper userRoleMapper, RoleMapper roleMapper) {
        this.userService = userService;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional
    public void register(RegisterRequestDTO request) {
        if (userService.getByUsername(request.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (userService.getByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists.");
        }
        userService.createDefaultUser(
                request.getUsername().trim(),
                request.getEmail().trim().toLowerCase(),
                request.getPassword(),
                request.getDisplayName().trim(),
                trimToNull(request.getPhone())
        );
    }

    @Override
    public LoginResponseVO login(LoginRequestDTO request) {
        String loginAccount = request.getLoginAccount().trim();
        User user = loginAccount.contains("@")
                ? userService.getByEmail(loginAccount.toLowerCase())
                : userService.getByUsername(loginAccount);

        if (user == null) {
            throw new IllegalArgumentException("Account does not exist.");
        }
        if (!"ACTIVE".equals(user.getAccountStatus())) {
            throw new IllegalArgumentException("This account is not active.");
        }

        String hashedPassword = PasswordUtil.hashPassword(request.getPassword());
        if (!hashedPassword.equals(user.getPasswordHash())) {
            throw new IllegalArgumentException("Password is incorrect.");
        }

        var assignments = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, user.getId())
                .orderByAsc(UserRole::getId));

        String primaryRole = "ROLE_USER";
        var roleKeys = assignments.stream()
                .map(assignment -> roleMapper.selectById(assignment.getRoleId()))
                .filter(role -> role != null && role.getRoleKey() != null && !role.getRoleKey().isBlank())
                .map(Role::getRoleKey)
                .distinct()
                .toList();

        if (!roleKeys.isEmpty()) {
            primaryRole = roleKeys.get(0);
        }

        return new LoginResponseVO(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getAccountStatus(),
                primaryRole,
                roleKeys
        );
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

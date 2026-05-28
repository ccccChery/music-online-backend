package com.sqa.musiconline.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqa.musiconline.entity.Role;
import com.sqa.musiconline.entity.User;
import com.sqa.musiconline.entity.UserRole;
import com.sqa.musiconline.mapper.RoleMapper;
import com.sqa.musiconline.mapper.UserMapper;
import com.sqa.musiconline.mapper.UserRoleMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class RequestUserContext {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    public RequestUserContext(UserMapper userMapper, UserRoleMapper userRoleMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
    }

    public CurrentUser requireUser(String userIdHeader) {
        if (!StringUtils.hasText(userIdHeader)) {
            throw new IllegalArgumentException("X-User-Id header is required.");
        }
        Long userId;
        try {
            userId = Long.valueOf(userIdHeader.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("X-User-Id must be a valid numeric user id.");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("Current user does not exist.");
        }
        if (!"ACTIVE".equals(user.getAccountStatus())) {
            throw new IllegalArgumentException("Current user is not active.");
        }

        List<String> roleKeys = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId))
                .stream()
                .map(assignment -> roleMapper.selectById(assignment.getRoleId()))
                .filter(role -> role != null && StringUtils.hasText(role.getRoleKey()))
                .map(Role::getRoleKey)
                .distinct()
                .toList();

        return new CurrentUser(user, roleKeys);
    }

    public record CurrentUser(User user, List<String> roleKeys) {

        public boolean hasRole(String roleKey) {
            return roleKeys.contains(roleKey);
        }
    }
}

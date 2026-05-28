package com.sqa.musiconline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqa.musiconline.entity.Role;
import com.sqa.musiconline.entity.User;
import com.sqa.musiconline.entity.UserRole;
import com.sqa.musiconline.mapper.RoleMapper;
import com.sqa.musiconline.mapper.UserMapper;
import com.sqa.musiconline.mapper.UserRoleMapper;
import com.sqa.musiconline.service.UserService;
import com.sqa.musiconline.utils.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    public UserServiceImpl(UserMapper userMapper, RoleMapper roleMapper, UserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .last("LIMIT 1"));
    }

    @Override
    public User getByEmail(String email) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email)
                .last("LIMIT 1"));
    }

    @Override
    @Transactional
    public User createDefaultUser(String username, String email, String rawPassword, String displayName, String phone) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(PasswordUtil.hashPassword(rawPassword));
        user.setDisplayName(displayName);
        user.setPhone(phone);
        user.setAccountStatus("ACTIVE");
        userMapper.insert(user);

        Role userRole = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleKey, "ROLE_USER")
                .last("LIMIT 1"));
        if (userRole == null) {
            throw new IllegalStateException("ROLE_USER is missing in roles table.");
        }

        UserRole assignment = new UserRole();
        assignment.setUserId(user.getId());
        assignment.setRoleId(userRole.getId());
        userRoleMapper.insert(assignment);
        return user;
    }
}

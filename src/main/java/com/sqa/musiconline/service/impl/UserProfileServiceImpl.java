package com.sqa.musiconline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqa.musiconline.config.RequestUserContext;
import com.sqa.musiconline.dto.UserProfileUpdateDTO;
import com.sqa.musiconline.entity.Role;
import com.sqa.musiconline.entity.User;
import com.sqa.musiconline.entity.UserRole;
import com.sqa.musiconline.mapper.RoleMapper;
import com.sqa.musiconline.mapper.UserMapper;
import com.sqa.musiconline.mapper.UserRoleMapper;
import com.sqa.musiconline.service.UserProfileService;
import com.sqa.musiconline.vo.UserProfileVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    public UserProfileServiceImpl(UserMapper userMapper, UserRoleMapper userRoleMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public UserProfileVO getProfile(RequestUserContext.CurrentUser currentUser) {
        return toProfileVO(currentUser.user());
    }

    @Override
    @Transactional
    public UserProfileVO updateProfile(RequestUserContext.CurrentUser currentUser, UserProfileUpdateDTO request) {
        User user = userMapper.selectById(currentUser.user().getId());
        if (user == null) {
            throw new IllegalArgumentException("Current user does not exist.");
        }

        user.setDisplayName(request.getDisplayName().trim());
        user.setPhone(trimToNull(request.getPhone()));
        user.setCountry(trimToNull(request.getCountry()));
        user.setCity(trimToNull(request.getCity()));
        user.setAddressLine1(trimToNull(request.getAddressLine1()));
        user.setAddressLine2(trimToNull(request.getAddressLine2()));
        user.setPostcode(trimToNull(request.getPostcode()));
        userMapper.updateById(user);

        return toProfileVO(user);
    }

    private UserProfileVO toProfileVO(User user) {
        List<String> roleKeys = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, user.getId()))
                .stream()
                .map(assignment -> roleMapper.selectById(assignment.getRoleId()))
                .filter(role -> role != null && StringUtils.hasText(role.getRoleKey()))
                .map(Role::getRoleKey)
                .distinct()
                .toList();

        return new UserProfileVO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getPhone(),
                user.getCountry(),
                user.getCity(),
                user.getAddressLine1(),
                user.getAddressLine2(),
                user.getPostcode(),
                user.getAccountStatus(),
                roleKeys
        );
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}

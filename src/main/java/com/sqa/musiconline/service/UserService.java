package com.sqa.musiconline.service;

import com.sqa.musiconline.entity.User;

public interface UserService {

    User getByUsername(String username);

    User getByEmail(String email);

    User createDefaultUser(String username, String email, String rawPassword, String displayName, String phone);
}

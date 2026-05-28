package com.sqa.musiconline.service;

import com.sqa.musiconline.config.RequestUserContext;
import com.sqa.musiconline.dto.UserProfileUpdateDTO;
import com.sqa.musiconline.vo.UserProfileVO;

public interface UserProfileService {

    UserProfileVO getProfile(RequestUserContext.CurrentUser currentUser);

    UserProfileVO updateProfile(RequestUserContext.CurrentUser currentUser, UserProfileUpdateDTO request);
}

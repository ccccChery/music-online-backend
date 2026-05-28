package com.sqa.musiconline.service;

import com.sqa.musiconline.dto.AdminLoginRequestDTO;
import com.sqa.musiconline.vo.AdminLoginResponseVO;

public interface AdminAuthService {

    AdminLoginResponseVO login(AdminLoginRequestDTO request);
}

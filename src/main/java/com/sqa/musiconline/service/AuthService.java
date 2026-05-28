package com.sqa.musiconline.service;

import com.sqa.musiconline.dto.LoginRequestDTO;
import com.sqa.musiconline.dto.RegisterRequestDTO;
import com.sqa.musiconline.vo.LoginResponseVO;

public interface AuthService {

    void register(RegisterRequestDTO request);

    LoginResponseVO login(LoginRequestDTO request);
}

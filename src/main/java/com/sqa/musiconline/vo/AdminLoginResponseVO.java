package com.sqa.musiconline.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminLoginResponseVO {

    private Long adminId;

    private String adminUsername;

    private String displayName;

    private String accountStatus;

    private String roleKey;
}

package com.sqa.musiconline.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LoginResponseVO {

    private Long userId;

    private String username;

    private String displayName;

    private String accountStatus;

    private String primaryRole;

    private List<String> roleKeys;
}

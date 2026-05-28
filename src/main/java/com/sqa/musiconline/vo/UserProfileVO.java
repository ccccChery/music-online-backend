package com.sqa.musiconline.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserProfileVO {

    private Long userId;

    private String username;

    private String email;

    private String displayName;

    private String phone;

    private String country;

    private String city;

    private String addressLine1;

    private String addressLine2;

    private String postcode;

    private String accountStatus;

    private List<String> roleKeys;
}

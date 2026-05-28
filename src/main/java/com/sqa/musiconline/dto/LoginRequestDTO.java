package com.sqa.musiconline.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(message = "Login account cannot be blank.")
    private String loginAccount;

    @NotBlank(message = "Password cannot be blank.")
    private String password;
}

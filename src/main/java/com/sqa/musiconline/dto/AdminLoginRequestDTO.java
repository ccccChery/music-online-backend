package com.sqa.musiconline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminLoginRequestDTO {

    @NotBlank(message = "Login account cannot be blank.")
    @Size(max = 100, message = "Login account must be 100 characters or fewer.")
    private String loginAccount;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 6, max = 50, message = "Password length must be between 6 and 50 characters.")
    private String password;
}

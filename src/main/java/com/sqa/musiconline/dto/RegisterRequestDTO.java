package com.sqa.musiconline.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {

    @NotBlank(message = "Username cannot be blank.")
    @Size(max = 50, message = "Username must be 50 characters or fewer.")
    private String username;

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email format is invalid.")
    @Size(max = 100, message = "Email must be 100 characters or fewer.")
    private String email;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 6, max = 50, message = "Password length must be between 6 and 50 characters.")
    private String password;

    @NotBlank(message = "Display name cannot be blank.")
    @Size(max = 100, message = "Display name must be 100 characters or fewer.")
    private String displayName;

    @Size(max = 30, message = "Phone must be 30 characters or fewer.")
    private String phone;
}

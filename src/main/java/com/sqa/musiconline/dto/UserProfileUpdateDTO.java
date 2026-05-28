package com.sqa.musiconline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateDTO {

    @NotBlank(message = "Display name cannot be blank.")
    @Size(max = 100, message = "Display name must be 100 characters or fewer.")
    private String displayName;

    @Size(max = 30, message = "Phone must be 30 characters or fewer.")
    private String phone;

    @Size(max = 100, message = "Country must be 100 characters or fewer.")
    private String country;

    @Size(max = 100, message = "City must be 100 characters or fewer.")
    private String city;

    @Size(max = 255, message = "Address line 1 must be 255 characters or fewer.")
    private String addressLine1;

    @Size(max = 255, message = "Address line 2 must be 255 characters or fewer.")
    private String addressLine2;

    @Size(max = 30, message = "Postcode must be 30 characters or fewer.")
    private String postcode;
}

package com.sqa.musiconline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRetailerApplicationReviewDTO {

    @NotBlank(message = "reviewStatus is required.")
    private String reviewStatus;

    @Size(max = 20, message = "feeStatus must be 20 characters or fewer.")
    private String feeStatus;

    @Size(max = 500, message = "reviewNote must be 500 characters or fewer.")
    private String reviewNote;
}

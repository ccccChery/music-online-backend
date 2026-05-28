package com.sqa.musiconline.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RetailerApplicationCreateDTO {

    @NotBlank(message = "Proposed store name cannot be blank.")
    @Size(max = 150, message = "Proposed store name must be 150 characters or fewer.")
    private String proposedStoreName;

    @Size(max = 100, message = "Business region must be 100 characters or fewer.")
    private String businessRegion;

    @NotNull(message = "Estimated inventory count is required.")
    @Positive(message = "Estimated inventory count must be positive.")
    private Integer estimatedInventoryCount;

    @Size(max = 2000, message = "Application message must be 2000 characters or fewer.")
    private String applicationMessage;

    @NotNull(message = "Fee amount is required.")
    @DecimalMin(value = "0.00", message = "Fee amount cannot be negative.")
    private BigDecimal feeAmountSnapshot;
}

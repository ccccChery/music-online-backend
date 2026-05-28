package com.sqa.musiconline.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemCreateDTO {

    @NotNull(message = "vinylId is required.")
    private Long vinylId;

    @NotNull(message = "quantity is required.")
    @Min(value = 1, message = "quantity must be at least 1.")
    private Integer quantity;
}

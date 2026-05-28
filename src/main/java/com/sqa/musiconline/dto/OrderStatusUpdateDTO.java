package com.sqa.musiconline.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusUpdateDTO {

    @NotBlank(message = "orderStatus is required.")
    private String orderStatus;
}

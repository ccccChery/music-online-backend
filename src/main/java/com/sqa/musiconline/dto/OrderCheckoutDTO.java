package com.sqa.musiconline.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCheckoutDTO {

    @NotBlank(message = "shippingRecipientName is required.")
    private String shippingRecipientName;

    @NotBlank(message = "shippingPhone is required.")
    private String shippingPhone;

    @NotBlank(message = "shippingCountry is required.")
    private String shippingCountry;

    @NotBlank(message = "shippingCity is required.")
    private String shippingCity;

    @NotBlank(message = "shippingAddressLine1 is required.")
    private String shippingAddressLine1;

    private String shippingAddressLine2;

    @NotBlank(message = "shippingPostcode is required.")
    private String shippingPostcode;

    private String buyerNote;
}

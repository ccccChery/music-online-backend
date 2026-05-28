package com.sqa.musiconline.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderSummaryVO {

    private Long orderId;

    private String orderNumber;

    private String buyerDisplayName;

    private String retailerDisplayName;

    private String orderStatus;

    private String paymentStatus;

    private BigDecimal subtotalAmount;

    private BigDecimal shippingAmount;

    private BigDecimal totalAmount;

    private String shippingRecipientName;

    private String shippingPhone;

    private String shippingCountry;

    private String shippingCity;

    private String shippingAddressLine1;

    private String shippingAddressLine2;

    private String shippingPostcode;

    private String buyerNote;

    private LocalDateTime orderedAt;

    private List<OrderItemVO> items;
}

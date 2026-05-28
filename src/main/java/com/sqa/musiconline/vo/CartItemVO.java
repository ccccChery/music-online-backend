package com.sqa.musiconline.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CartItemVO {

    private Long cartItemId;

    private Long vinylId;

    private Long sellerUserId;

    private String sellerDisplayName;

    private String title;

    private String artistName;

    private String formatType;

    private String conditionGrade;

    private BigDecimal price;

    private Integer quantity;

    private Integer stockQuantity;

    private String listingStatus;

    private String coverImageUrl;

    private BigDecimal lineTotalAmount;
}

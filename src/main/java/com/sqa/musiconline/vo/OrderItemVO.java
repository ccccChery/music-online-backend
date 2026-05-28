package com.sqa.musiconline.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class OrderItemVO {

    private Long orderItemId;

    private Long vinylId;

    private String vinylTitle;

    private String artistName;

    private BigDecimal unitPrice;

    private Integer quantity;

    private BigDecimal lineTotalAmount;
}

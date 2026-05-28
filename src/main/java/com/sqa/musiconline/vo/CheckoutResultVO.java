package com.sqa.musiconline.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class CheckoutResultVO {

    private Integer createdOrderCount;

    private List<String> orderNumbers;

    private BigDecimal totalAmount;
}

package com.sqa.musiconline.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class CartViewVO {

    private Long cartId;

    private Integer itemCount;

    private BigDecimal subtotalAmount;

    private List<CartItemVO> items;
}

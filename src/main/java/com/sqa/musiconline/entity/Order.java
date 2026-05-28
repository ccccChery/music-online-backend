package com.sqa.musiconline.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_number")
    private String orderNumber;

    @TableField("buyer_user_id")
    private Long buyerUserId;

    @TableField("retailer_user_id")
    private Long retailerUserId;

    @TableField("order_status")
    private String orderStatus;

    @TableField("payment_status")
    private String paymentStatus;

    @TableField("subtotal_amount")
    private BigDecimal subtotalAmount;

    @TableField("shipping_amount")
    private BigDecimal shippingAmount;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("shipping_recipient_name")
    private String shippingRecipientName;

    @TableField("shipping_phone")
    private String shippingPhone;

    @TableField("shipping_country")
    private String shippingCountry;

    @TableField("shipping_city")
    private String shippingCity;

    @TableField("shipping_address_line1")
    private String shippingAddressLine1;

    @TableField("shipping_address_line2")
    private String shippingAddressLine2;

    @TableField("shipping_postcode")
    private String shippingPostcode;

    @TableField("buyer_note")
    private String buyerNote;

    @TableField("ordered_at")
    private LocalDateTime orderedAt;

    @TableField("paid_at")
    private LocalDateTime paidAt;

    @TableField("fulfilled_at")
    private LocalDateTime fulfilledAt;

    @TableField("cancelled_at")
    private LocalDateTime cancelledAt;
}

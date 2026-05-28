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
@TableName("order_items")
public class OrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("vinyl_id")
    private Long vinylId;

    @TableField("vinyl_title_snapshot")
    private String vinylTitleSnapshot;

    @TableField("artist_name_snapshot")
    private String artistNameSnapshot;

    @TableField("unit_price_snapshot")
    private BigDecimal unitPriceSnapshot;

    private Integer quantity;

    @TableField("line_total_amount")
    private BigDecimal lineTotalAmount;

    @TableField("created_at")
    private LocalDateTime createdAt;
}

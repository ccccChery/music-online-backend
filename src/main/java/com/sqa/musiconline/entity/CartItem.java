package com.sqa.musiconline.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("cart_items")
public class CartItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("cart_id")
    private Long cartId;

    @TableField("vinyl_id")
    private Long vinylId;

    private Integer quantity;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}

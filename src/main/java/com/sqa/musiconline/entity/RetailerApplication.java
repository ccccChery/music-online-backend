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
@TableName("retailer_applications")
public class RetailerApplication {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("proposed_store_name")
    private String proposedStoreName;

    @TableField("business_region")
    private String businessRegion;

    @TableField("estimated_inventory_count")
    private Integer estimatedInventoryCount;

    @TableField("application_message")
    private String applicationMessage;

    @TableField("fee_amount_snapshot")
    private BigDecimal feeAmountSnapshot;

    @TableField("fee_status")
    private String feeStatus;

    @TableField("review_status")
    private String reviewStatus;

    @TableField("review_note")
    private String reviewNote;

    @TableField("submitted_at")
    private LocalDateTime submittedAt;

    @TableField("reviewed_at")
    private LocalDateTime reviewedAt;

    @TableField("reviewed_by_admin_id")
    private Long reviewedByAdminId;
}

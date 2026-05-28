package com.sqa.musiconline.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("vinyls")
public class Vinyl {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("seller_user_id")
    private Long sellerUserId;

    @TableField("artist_name")
    private String artistName;

    private String title;

    @TableField("format_type")
    private String formatType;

    @TableField("genre_name")
    private String genreName;

    @TableField("condition_grade")
    private String conditionGrade;

    @TableField("release_date")
    private LocalDate releaseDate;

    private BigDecimal price;

    @TableField("stock_quantity")
    private Integer stockQuantity;

    private String description;

    @TableField("cover_image_url")
    private String coverImageUrl;

    @TableField("listing_status")
    private String listingStatus;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}

package com.sqa.musiconline.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminVinylOverviewVO {

    private Long id;

    private Long sellerUserId;

    private String sellerDisplayName;

    private String artistName;

    private String title;

    private String formatType;

    private String genreName;

    private String conditionGrade;

    private LocalDate releaseDate;

    private BigDecimal price;

    private Integer stockQuantity;

    private String listingStatus;

    private String description;

    private String coverImageUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

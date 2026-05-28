package com.sqa.musiconline.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class VinylDetailVO {

    private Long id;

    private Long sellerUserId;

    private String artistName;

    private String title;

    private String formatType;

    private String genreName;

    private String conditionGrade;

    private LocalDate releaseDate;

    private BigDecimal price;

    private Integer stockQuantity;

    private String description;

    private String coverImageUrl;
}

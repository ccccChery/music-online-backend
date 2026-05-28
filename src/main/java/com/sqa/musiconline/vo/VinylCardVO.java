package com.sqa.musiconline.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class VinylCardVO {

    private Long id;

    private String artistName;

    private String title;

    private String formatType;

    private String genreName;

    private String conditionGrade;

    private BigDecimal price;

    private String coverImageUrl;

    private Integer stockQuantity;
}

package com.sqa.musiconline.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class RetailerVinylSaveDTO {

    @NotBlank(message = "Artist name cannot be blank.")
    @Size(max = 150, message = "Artist name must be 150 characters or fewer.")
    private String artistName;

    @NotBlank(message = "Title cannot be blank.")
    @Size(max = 200, message = "Title must be 200 characters or fewer.")
    private String title;

    @NotBlank(message = "Format type cannot be blank.")
    private String formatType;

    @Size(max = 80, message = "Genre name must be 80 characters or fewer.")
    private String genreName;

    @NotBlank(message = "Condition grade cannot be blank.")
    @Size(max = 20, message = "Condition grade must be 20 characters or fewer.")
    private String conditionGrade;

    private LocalDate releaseDate;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.00", message = "Price cannot be negative.")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required.")
    @PositiveOrZero(message = "Stock quantity cannot be negative.")
    private Integer stockQuantity;

    private String description;

    @Size(max = 500, message = "Cover image URL must be 500 characters or fewer.")
    private String coverImageUrl;
}

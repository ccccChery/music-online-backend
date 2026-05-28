package com.sqa.musiconline.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VinylSearchRequestDTO {

    private String keyword;

    private String artistName;

    private String title;

    private String formatType;

    private String genreName;
}

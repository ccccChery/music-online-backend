package com.sqa.musiconline.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminRetailerApplicationVO {

    private Long id;

    private Long userId;

    private String username;

    private String userDisplayName;

    private String userEmail;

    private String userPhone;

    private String proposedStoreName;

    private String businessRegion;

    private Integer estimatedInventoryCount;

    private String applicationMessage;

    private BigDecimal feeAmountSnapshot;

    private String feeStatus;

    private String reviewStatus;

    private String reviewNote;

    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;

    private Long reviewedByAdminId;

    private String reviewedByAdminDisplayName;
}

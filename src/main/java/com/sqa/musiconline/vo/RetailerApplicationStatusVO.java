package com.sqa.musiconline.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RetailerApplicationStatusVO {

    private Long id;

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

    private boolean feePaymentAllowed;
}

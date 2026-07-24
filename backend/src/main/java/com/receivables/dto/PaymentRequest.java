package com.receivables.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentRequest {
    @NotNull
    private Long receivableId;

    @NotNull
    private LocalDate paymentDate;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    private String method;
    private String referenceNo;
    private String note;
}

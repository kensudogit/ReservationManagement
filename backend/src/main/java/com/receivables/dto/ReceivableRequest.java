package com.receivables.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReceivableRequest {
    @NotBlank
    private String invoiceNo;

    @NotNull
    private Long customerId;

    @NotNull
    private LocalDate invoiceDate;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    private String currency;
    private String description;
}

package com.receivables.dto;

import com.receivables.entity.Receivable;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ReceivableResponse {
    private Long id;
    private String invoiceNo;
    private Long customerId;
    private String customerCode;
    private String customerName;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal amount;
    private BigDecimal balance;
    private String currency;
    private String status;
    private String description;
    private LocalDateTime createdAt;

    public static ReceivableResponse from(Receivable receivable) {
        return ReceivableResponse.builder()
                .id(receivable.getId())
                .invoiceNo(receivable.getInvoiceNo())
                .customerId(receivable.getCustomer().getId())
                .customerCode(receivable.getCustomer().getCustomerCode())
                .customerName(receivable.getCustomer().getName())
                .invoiceDate(receivable.getInvoiceDate())
                .dueDate(receivable.getDueDate())
                .amount(receivable.getAmount())
                .balance(receivable.getBalance())
                .currency(receivable.getCurrency())
                .status(receivable.getStatus())
                .description(receivable.getDescription())
                .createdAt(receivable.getCreatedAt())
                .build();
    }
}

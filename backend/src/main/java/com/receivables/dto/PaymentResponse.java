package com.receivables.dto;

import com.receivables.entity.Payment;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private Long receivableId;
    private String invoiceNo;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private String method;
    private String referenceNo;
    private String note;
    private LocalDateTime createdAt;

    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .receivableId(payment.getReceivable().getId())
                .invoiceNo(payment.getReceivable().getInvoiceNo())
                .paymentDate(payment.getPaymentDate())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .referenceNo(payment.getReferenceNo())
                .note(payment.getNote())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}

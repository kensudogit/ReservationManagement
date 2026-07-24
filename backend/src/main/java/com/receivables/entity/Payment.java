package com.receivables.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RECEIVABLE_ID", nullable = false)
    private Receivable receivable;

    @Column(name = "PAYMENT_DATE", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "AMOUNT", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "METHOD", nullable = false, length = 30)
    private String method;

    @Column(name = "REFERENCE_NO", length = 80)
    private String referenceNo;

    @Column(name = "NOTE", length = 500)
    private String note;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        if (method == null) {
            method = "BANK_TRANSFER";
        }
    }
}

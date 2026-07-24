package com.receivables.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "RECEIVABLES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receivable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "INVOICE_NO", nullable = false, unique = true, length = 40)
    private String invoiceNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private Customer customer;

    @Column(name = "INVOICE_DATE", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "DUE_DATE", nullable = false)
    private LocalDate dueDate;

    @Column(name = "AMOUNT", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "BALANCE", nullable = false, precision = 18, scale = 2)
    private BigDecimal balance;

    @Column(name = "CURRENCY", nullable = false, length = 3)
    private String currency;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (currency == null) {
            currency = "JPY";
        }
        if (status == null) {
            status = "OPEN";
        }
        if (balance == null) {
            balance = amount;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

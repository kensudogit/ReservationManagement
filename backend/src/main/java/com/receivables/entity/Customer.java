package com.receivables.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "CUSTOMERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CUSTOMER_CODE", nullable = false, unique = true, length = 32)
    private String customerCode;

    @Column(name = "NAME", nullable = false, length = 200)
    private String name;

    @Column(name = "CONTACT_NAME", length = 100)
    private String contactName;

    @Column(name = "EMAIL", length = 200)
    private String email;

    @Column(name = "PHONE", length = 50)
    private String phone;

    @Column(name = "CREDIT_LIMIT")
    private Long creditLimit;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (status == null) {
            status = "ACTIVE";
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

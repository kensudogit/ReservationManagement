package com.receivables.dto;

import com.receivables.entity.Customer;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CustomerResponse {
    private Long id;
    private String customerCode;
    private String name;
    private String contactName;
    private String email;
    private String phone;
    private Long creditLimit;
    private String status;
    private LocalDateTime createdAt;

    public static CustomerResponse from(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .customerCode(customer.getCustomerCode())
                .name(customer.getName())
                .contactName(customer.getContactName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .creditLimit(customer.getCreditLimit())
                .status(customer.getStatus())
                .createdAt(customer.getCreatedAt())
                .build();
    }
}

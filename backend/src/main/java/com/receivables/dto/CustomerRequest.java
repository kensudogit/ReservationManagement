package com.receivables.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CustomerRequest {
    @NotBlank
    private String customerCode;

    @NotBlank
    private String name;

    private String contactName;
    private String email;
    private String phone;

    @PositiveOrZero
    private Long creditLimit;

    private String status;
}

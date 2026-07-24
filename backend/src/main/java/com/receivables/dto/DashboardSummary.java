package com.receivables.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardSummary {
    private long customerCount;
    private long openReceivableCount;
    private long overdueCount;
    private BigDecimal totalOpenBalance;
}

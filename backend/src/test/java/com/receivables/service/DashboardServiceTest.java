package com.receivables.service;

import com.receivables.dto.DashboardSummary;
import com.receivables.repository.CustomerRepository;
import com.receivables.repository.ReceivableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ReceivableRepository receivableRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void summaryAggregatesCountsAndBalance() {
        when(customerRepository.count()).thenReturn(5L);
        when(receivableRepository.countByStatus("OPEN")).thenReturn(2L);
        when(receivableRepository.countByStatus("PARTIAL")).thenReturn(1L);
        when(receivableRepository.countByStatus("OVERDUE")).thenReturn(3L);
        when(receivableRepository.sumOpenBalance()).thenReturn(new BigDecimal("2500000"));

        DashboardSummary summary = dashboardService.summary();

        assertThat(summary.getCustomerCount()).isEqualTo(5L);
        assertThat(summary.getOpenReceivableCount()).isEqualTo(6L);
        assertThat(summary.getOverdueCount()).isEqualTo(3L);
        assertThat(summary.getTotalOpenBalance()).isEqualByComparingTo("2500000");
    }
}

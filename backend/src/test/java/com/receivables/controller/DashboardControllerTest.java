package com.receivables.controller;

import com.receivables.dto.DashboardSummary;
import com.receivables.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Test
    void summaryReturnsDashboard() throws Exception {
        when(dashboardService.summary()).thenReturn(DashboardSummary.builder()
                .customerCount(2L)
                .openReceivableCount(3L)
                .overdueCount(1L)
                .totalOpenBalance(new BigDecimal("1730000"))
                .build());

        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerCount").value(2))
                .andExpect(jsonPath("$.openReceivableCount").value(3))
                .andExpect(jsonPath("$.overdueCount").value(1))
                .andExpect(jsonPath("$.totalOpenBalance").value(1730000));
    }
}

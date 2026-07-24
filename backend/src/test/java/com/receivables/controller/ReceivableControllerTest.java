package com.receivables.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.receivables.dto.ReceivableRequest;
import com.receivables.dto.ReceivableResponse;
import com.receivables.service.ReceivableService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReceivableController.class)
class ReceivableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReceivableService receivableService;

    private ReceivableResponse sample() {
        return ReceivableResponse.builder()
                .id(1L)
                .invoiceNo("INV-001")
                .customerId(1L)
                .customerCode("C001")
                .customerName("株式会社テスト")
                .invoiceDate(LocalDate.of(2026, 7, 1))
                .dueDate(LocalDate.of(2026, 8, 1))
                .amount(new BigDecimal("100000.00"))
                .balance(new BigDecimal("100000.00"))
                .currency("JPY")
                .status("OPEN")
                .description("7月請求")
                .createdAt(LocalDateTime.of(2026, 7, 1, 10, 0))
                .build();
    }

    @Test
    void findAllReturnsList() throws Exception {
        when(receivableService.findAll()).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/receivables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].invoiceNo").value("INV-001"))
                .andExpect(jsonPath("$[0].status").value("OPEN"));
    }

    @Test
    void findByIdReturnsReceivable() throws Exception {
        when(receivableService.findById(1L)).thenReturn(sample());

        mockMvc.perform(get("/api/receivables/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createReturnsCreated() throws Exception {
        when(receivableService.create(any(ReceivableRequest.class))).thenReturn(sample());

        ReceivableRequest request = new ReceivableRequest();
        request.setInvoiceNo("INV-001");
        request.setCustomerId(1L);
        request.setInvoiceDate(LocalDate.of(2026, 7, 1));
        request.setDueDate(LocalDate.of(2026, 8, 1));
        request.setAmount(new BigDecimal("100000.00"));

        mockMvc.perform(post("/api/receivables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.invoiceNo").value("INV-001"));
    }

    @Test
    void createWithInvalidAmountReturnsBadRequest() throws Exception {
        ReceivableRequest request = new ReceivableRequest();
        request.setInvoiceNo("INV-001");
        request.setCustomerId(1L);
        request.setInvoiceDate(LocalDate.of(2026, 7, 1));
        request.setDueDate(LocalDate.of(2026, 8, 1));
        request.setAmount(BigDecimal.ZERO);

        mockMvc.perform(post("/api/receivables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createConflictPropagates() throws Exception {
        when(receivableService.create(any(ReceivableRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "請求番号が既に存在します"));

        ReceivableRequest request = new ReceivableRequest();
        request.setInvoiceNo("INV-001");
        request.setCustomerId(1L);
        request.setInvoiceDate(LocalDate.of(2026, 7, 1));
        request.setDueDate(LocalDate.of(2026, 8, 1));
        request.setAmount(new BigDecimal("100000.00"));

        mockMvc.perform(post("/api/receivables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}

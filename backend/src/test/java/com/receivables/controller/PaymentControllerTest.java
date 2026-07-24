package com.receivables.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.receivables.dto.PaymentRequest;
import com.receivables.dto.PaymentResponse;
import com.receivables.service.PaymentService;
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

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    private PaymentResponse sample() {
        return PaymentResponse.builder()
                .id(1L)
                .receivableId(10L)
                .invoiceNo("INV-001")
                .paymentDate(LocalDate.of(2026, 7, 20))
                .amount(new BigDecimal("40000.00"))
                .method("BANK_TRANSFER")
                .referenceNo("REF-1")
                .note("一部入金")
                .createdAt(LocalDateTime.of(2026, 7, 20, 12, 0))
                .build();
    }

    @Test
    void findAllReturnsList() throws Exception {
        when(paymentService.findAll()).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].invoiceNo").value("INV-001"));
    }

    @Test
    void findByReceivableReturnsList() throws Exception {
        when(paymentService.findByReceivable(10L)).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/payments/receivable/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].receivableId").value(10));
    }

    @Test
    void createReturnsCreated() throws Exception {
        when(paymentService.create(any(PaymentRequest.class))).thenReturn(sample());

        PaymentRequest request = new PaymentRequest();
        request.setReceivableId(10L);
        request.setPaymentDate(LocalDate.of(2026, 7, 20));
        request.setAmount(new BigDecimal("40000.00"));

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.method").value("BANK_TRANSFER"));
    }

    @Test
    void createOverpayPropagatesBadRequest() throws Exception {
        when(paymentService.create(any(PaymentRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "入金額が残高を超えています"));

        PaymentRequest request = new PaymentRequest();
        request.setReceivableId(10L);
        request.setPaymentDate(LocalDate.of(2026, 7, 20));
        request.setAmount(new BigDecimal("999999.00"));

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

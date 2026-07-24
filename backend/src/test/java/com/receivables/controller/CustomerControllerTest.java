package com.receivables.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.receivables.dto.CustomerRequest;
import com.receivables.dto.CustomerResponse;
import com.receivables.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private CustomerResponse sample() {
        return CustomerResponse.builder()
                .id(1L)
                .customerCode("C001")
                .name("株式会社テスト")
                .contactName("山田")
                .email("test@example.com")
                .phone("03-1111-2222")
                .creditLimit(1_000_000L)
                .status("ACTIVE")
                .createdAt(LocalDateTime.of(2026, 7, 1, 10, 0))
                .build();
    }

    @Test
    void findAllReturnsList() throws Exception {
        when(customerService.findAll()).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerCode").value("C001"))
                .andExpect(jsonPath("$[0].name").value("株式会社テスト"));
    }

    @Test
    void findByIdReturnsCustomer() throws Exception {
        when(customerService.findById(1L)).thenReturn(sample());

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createReturnsCreated() throws Exception {
        when(customerService.create(any(CustomerRequest.class))).thenReturn(sample());

        CustomerRequest request = new CustomerRequest();
        request.setCustomerCode("C001");
        request.setName("株式会社テスト");
        request.setCreditLimit(1_000_000L);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerCode").value("C001"));
    }

    @Test
    void createWithBlankCodeReturnsBadRequest() throws Exception {
        CustomerRequest request = new CustomerRequest();
        request.setCustomerCode("");
        request.setName("テスト");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createConflictPropagates() throws Exception {
        when(customerService.create(any(CustomerRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "得意先コードが既に存在します"));

        CustomerRequest request = new CustomerRequest();
        request.setCustomerCode("C001");
        request.setName("株式会社テスト");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateReturnsOk() throws Exception {
        when(customerService.update(eq(1L), any(CustomerRequest.class))).thenReturn(sample());

        CustomerRequest request = new CustomerRequest();
        request.setCustomerCode("C001");
        request.setName("更新名");

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerCode").value("C001"));
    }
}

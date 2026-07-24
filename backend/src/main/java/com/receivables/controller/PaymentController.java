package com.receivables.controller;

import com.receivables.dto.PaymentRequest;
import com.receivables.dto.PaymentResponse;
import com.receivables.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentResponse> list() {
        return paymentService.findAll();
    }

    @GetMapping("/receivable/{receivableId}")
    public List<PaymentResponse> byReceivable(@PathVariable Long receivableId) {
        return paymentService.findByReceivable(receivableId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse create(@Valid @RequestBody PaymentRequest request) {
        return paymentService.create(request);
    }
}

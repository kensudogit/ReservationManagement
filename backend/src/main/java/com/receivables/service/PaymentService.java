package com.receivables.service;

import com.receivables.dto.PaymentRequest;
import com.receivables.dto.PaymentResponse;
import com.receivables.entity.Payment;
import com.receivables.entity.Receivable;
import com.receivables.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReceivableService receivableService;

    @Transactional(readOnly = true)
    public List<PaymentResponse> findAll() {
        return paymentRepository.findAll().stream().map(PaymentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findByReceivable(Long receivableId) {
        return paymentRepository.findByReceivableIdOrderByPaymentDateDesc(receivableId)
                .stream()
                .map(PaymentResponse::from)
                .toList();
    }

    @Transactional
    public PaymentResponse create(PaymentRequest request) {
        Receivable receivable = receivableService.getEntity(request.getReceivableId());
        Payment payment = Payment.builder()
                .receivable(receivable)
                .paymentDate(request.getPaymentDate())
                .amount(request.getAmount())
                .method(request.getMethod() != null ? request.getMethod() : "BANK_TRANSFER")
                .referenceNo(request.getReferenceNo())
                .note(request.getNote())
                .build();
        Payment saved = paymentRepository.save(payment);
        receivableService.applyPayment(receivable, request.getAmount());
        return PaymentResponse.from(saved);
    }
}

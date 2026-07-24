package com.receivables.service;

import com.receivables.dto.PaymentRequest;
import com.receivables.dto.PaymentResponse;
import com.receivables.entity.Customer;
import com.receivables.entity.Payment;
import com.receivables.entity.Receivable;
import com.receivables.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReceivableService receivableService;

    @InjectMocks
    private PaymentService paymentService;

    private Receivable receivable() {
        Customer customer = Customer.builder().id(1L).customerCode("C001").name("テスト").status("ACTIVE").build();
        return Receivable.builder()
                .id(10L)
                .invoiceNo("INV-001")
                .customer(customer)
                .invoiceDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(10))
                .amount(new BigDecimal("100000"))
                .balance(new BigDecimal("100000"))
                .currency("JPY")
                .status("OPEN")
                .build();
    }

    @Test
    void createSavesPaymentAndAppliesToReceivable() {
        Receivable receivable = receivable();
        when(receivableService.getEntity(10L)).thenReturn(receivable);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(5L);
            return p;
        });

        PaymentRequest request = new PaymentRequest();
        request.setReceivableId(10L);
        request.setPaymentDate(LocalDate.now());
        request.setAmount(new BigDecimal("40000"));

        PaymentResponse response = paymentService.create(request);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        assertThat(captor.getValue().getMethod()).isEqualTo("BANK_TRANSFER");
        verify(receivableService).applyPayment(eq(receivable), eq(new BigDecimal("40000")));
        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getInvoiceNo()).isEqualTo("INV-001");
    }

    @Test
    void findByReceivableDelegatesToRepository() {
        Receivable receivable = receivable();
        Payment payment = Payment.builder()
                .id(1L)
                .receivable(receivable)
                .paymentDate(LocalDate.now())
                .amount(new BigDecimal("1000"))
                .method("CASH")
                .build();
        when(paymentRepository.findByReceivableIdOrderByPaymentDateDesc(10L)).thenReturn(List.of(payment));

        List<PaymentResponse> result = paymentService.findByReceivable(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMethod()).isEqualTo("CASH");
    }
}

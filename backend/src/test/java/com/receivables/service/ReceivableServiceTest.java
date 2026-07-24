package com.receivables.service;

import com.receivables.dto.ReceivableRequest;
import com.receivables.dto.ReceivableResponse;
import com.receivables.entity.Customer;
import com.receivables.entity.Receivable;
import com.receivables.repository.ReceivableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceivableServiceTest {

    @Mock
    private ReceivableRepository receivableRepository;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private ReceivableService receivableService;

    private Customer customer() {
        return Customer.builder()
                .id(1L)
                .customerCode("C001")
                .name("株式会社テスト")
                .status("ACTIVE")
                .build();
    }

    @Test
    void createSetsOpenWhenDueInFuture() {
        when(receivableRepository.findByInvoiceNo("INV-100")).thenReturn(Optional.empty());
        when(customerService.getEntity(1L)).thenReturn(customer());
        when(receivableRepository.save(any(Receivable.class))).thenAnswer(invocation -> {
            Receivable r = invocation.getArgument(0);
            r.setId(10L);
            return r;
        });

        ReceivableRequest request = new ReceivableRequest();
        request.setInvoiceNo("INV-100");
        request.setCustomerId(1L);
        request.setInvoiceDate(LocalDate.now());
        request.setDueDate(LocalDate.now().plusDays(10));
        request.setAmount(new BigDecimal("100000"));

        ReceivableResponse response = receivableService.create(request);

        ArgumentCaptor<Receivable> captor = ArgumentCaptor.forClass(Receivable.class);
        verify(receivableRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("OPEN");
        assertThat(captor.getValue().getBalance()).isEqualByComparingTo("100000");
        assertThat(response.getInvoiceNo()).isEqualTo("INV-100");
    }

    @Test
    void createSetsOverdueWhenDueInPast() {
        when(receivableRepository.findByInvoiceNo("INV-200")).thenReturn(Optional.empty());
        when(customerService.getEntity(1L)).thenReturn(customer());
        when(receivableRepository.save(any(Receivable.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReceivableRequest request = new ReceivableRequest();
        request.setInvoiceNo("INV-200");
        request.setCustomerId(1L);
        request.setInvoiceDate(LocalDate.now().minusDays(30));
        request.setDueDate(LocalDate.now().minusDays(1));
        request.setAmount(new BigDecimal("50000"));

        receivableService.create(request);

        ArgumentCaptor<Receivable> captor = ArgumentCaptor.forClass(Receivable.class);
        verify(receivableRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("OVERDUE");
    }

    @Test
    void createThrowsConflictOnDuplicateInvoice() {
        when(receivableRepository.findByInvoiceNo("INV-100"))
                .thenReturn(Optional.of(Receivable.builder().id(1L).invoiceNo("INV-100").build()));

        ReceivableRequest request = new ReceivableRequest();
        request.setInvoiceNo("INV-100");
        request.setCustomerId(1L);
        request.setInvoiceDate(LocalDate.now());
        request.setDueDate(LocalDate.now().plusDays(5));
        request.setAmount(new BigDecimal("1000"));

        assertThatThrownBy(() -> receivableService.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void applyPaymentClosesWhenBalanceBecomesZero() {
        Receivable receivable = Receivable.builder()
                .id(1L)
                .invoiceNo("INV-1")
                .customer(customer())
                .dueDate(LocalDate.now().plusDays(5))
                .amount(new BigDecimal("1000"))
                .balance(new BigDecimal("400"))
                .status("PARTIAL")
                .currency("JPY")
                .build();

        receivableService.applyPayment(receivable, new BigDecimal("400"));

        assertThat(receivable.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(receivable.getStatus()).isEqualTo("CLOSED");
        verify(receivableRepository).save(receivable);
    }

    @Test
    void applyPaymentSetsPartialWhenDueInFuture() {
        Receivable receivable = Receivable.builder()
                .id(1L)
                .invoiceNo("INV-1")
                .customer(customer())
                .dueDate(LocalDate.now().plusDays(5))
                .amount(new BigDecimal("1000"))
                .balance(new BigDecimal("1000"))
                .status("OPEN")
                .currency("JPY")
                .build();

        receivableService.applyPayment(receivable, new BigDecimal("300"));

        assertThat(receivable.getBalance()).isEqualByComparingTo("700");
        assertThat(receivable.getStatus()).isEqualTo("PARTIAL");
    }

    @Test
    void applyPaymentSetsOverdueWhenDueInPastAndBalanceRemains() {
        Receivable receivable = Receivable.builder()
                .id(1L)
                .invoiceNo("INV-1")
                .customer(customer())
                .dueDate(LocalDate.now().minusDays(2))
                .amount(new BigDecimal("1000"))
                .balance(new BigDecimal("1000"))
                .status("OVERDUE")
                .currency("JPY")
                .build();

        receivableService.applyPayment(receivable, new BigDecimal("100"));

        assertThat(receivable.getStatus()).isEqualTo("OVERDUE");
        assertThat(receivable.getBalance()).isEqualByComparingTo("900");
    }

    @Test
    void applyPaymentRejectsOverpay() {
        Receivable receivable = Receivable.builder()
                .id(1L)
                .invoiceNo("INV-1")
                .customer(customer())
                .dueDate(LocalDate.now().plusDays(5))
                .amount(new BigDecimal("1000"))
                .balance(new BigDecimal("100"))
                .status("PARTIAL")
                .currency("JPY")
                .build();

        assertThatThrownBy(() -> receivableService.applyPayment(receivable, new BigDecimal("200")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(rse.getReason()).isEqualTo("入金額が残高を超えています");
                });
    }
}

package com.receivables.repository;

import com.receivables.entity.Customer;
import com.receivables.entity.Payment;
import com.receivables.entity.Receivable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PaymentRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ReceivableRepository receivableRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private Receivable receivable;

    @BeforeEach
    void setUp() {
        Customer customer = customerRepository.save(Customer.builder()
                .customerCode("C001")
                .name("入金テスト")
                .status("ACTIVE")
                .creditLimit(1000L)
                .build());
        receivable = receivableRepository.save(Receivable.builder()
                .invoiceNo("INV-PAY")
                .customer(customer)
                .invoiceDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(5))
                .amount(new BigDecimal("10000"))
                .balance(new BigDecimal("10000"))
                .currency("JPY")
                .status("OPEN")
                .build());
    }

    @Test
    void findByReceivableIdOrderByPaymentDateDesc() {
        paymentRepository.save(Payment.builder()
                .receivable(receivable)
                .paymentDate(LocalDate.of(2026, 7, 1))
                .amount(new BigDecimal("1000"))
                .method("CASH")
                .build());
        paymentRepository.save(Payment.builder()
                .receivable(receivable)
                .paymentDate(LocalDate.of(2026, 7, 10))
                .amount(new BigDecimal("2000"))
                .method("BANK_TRANSFER")
                .build());

        List<Payment> payments = paymentRepository.findByReceivableIdOrderByPaymentDateDesc(receivable.getId());

        assertThat(payments).hasSize(2);
        assertThat(payments.get(0).getPaymentDate()).isEqualTo(LocalDate.of(2026, 7, 10));
        assertThat(payments.get(1).getPaymentDate()).isEqualTo(LocalDate.of(2026, 7, 1));
    }
}

package com.receivables.repository;

import com.receivables.entity.Customer;
import com.receivables.entity.Receivable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ReceivableRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ReceivableRepository receivableRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = customerRepository.save(Customer.builder()
                .customerCode("C001")
                .name("リポジトリテスト")
                .status("ACTIVE")
                .creditLimit(1_000_000L)
                .build());
    }

    @Test
    void sumOpenBalanceExcludesClosed() {
        receivableRepository.save(openReceivable("INV-A", "OPEN", "1000"));
        receivableRepository.save(openReceivable("INV-B", "PARTIAL", "500"));
        receivableRepository.save(openReceivable("INV-C", "CLOSED", "0"));

        assertThat(receivableRepository.sumOpenBalance()).isEqualByComparingTo("1500");
    }

    @Test
    void countByStatus() {
        receivableRepository.save(openReceivable("INV-O1", "OPEN", "100"));
        receivableRepository.save(openReceivable("INV-O2", "OPEN", "200"));
        receivableRepository.save(openReceivable("INV-D1", "OVERDUE", "300"));

        assertThat(receivableRepository.countByStatus("OPEN")).isEqualTo(2);
        assertThat(receivableRepository.countByStatus("OVERDUE")).isEqualTo(1);
    }

    @Test
    void findByInvoiceNo() {
        receivableRepository.save(openReceivable("INV-UNIQUE", "OPEN", "999"));

        assertThat(receivableRepository.findByInvoiceNo("INV-UNIQUE")).isPresent();
        assertThat(receivableRepository.findByInvoiceNo("NONE")).isEmpty();
    }

    private Receivable openReceivable(String invoiceNo, String status, String balance) {
        return Receivable.builder()
                .invoiceNo(invoiceNo)
                .customer(customer)
                .invoiceDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(10))
                .amount(new BigDecimal(balance).max(new BigDecimal("1")))
                .balance(new BigDecimal(balance))
                .currency("JPY")
                .status(status)
                .build();
    }
}

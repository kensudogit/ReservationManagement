package com.receivables.repository;

import com.receivables.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void existsByCustomerCode() {
        customerRepository.save(Customer.builder()
                .customerCode("C100")
                .name("存在確認")
                .status("ACTIVE")
                .creditLimit(0L)
                .build());

        assertThat(customerRepository.existsByCustomerCode("C100")).isTrue();
        assertThat(customerRepository.existsByCustomerCode("MISSING")).isFalse();
    }

    @Test
    void findByCustomerCode() {
        customerRepository.save(Customer.builder()
                .customerCode("C200")
                .name("検索")
                .status("ACTIVE")
                .creditLimit(10L)
                .build());

        assertThat(customerRepository.findByCustomerCode("C200"))
                .isPresent()
                .get()
                .extracting(Customer::getName)
                .isEqualTo("検索");
    }

    @Test
    void prePersistSetsDefaults() {
        Customer saved = customerRepository.save(Customer.builder()
                .customerCode("C300")
                .name("ライフサイクル")
                .build());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo("ACTIVE");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}

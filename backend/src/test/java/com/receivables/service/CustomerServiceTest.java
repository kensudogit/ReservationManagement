package com.receivables.service;

import com.receivables.dto.CustomerRequest;
import com.receivables.dto.CustomerResponse;
import com.receivables.entity.Customer;
import com.receivables.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void findAllMapsEntities() {
        Customer customer = Customer.builder()
                .id(1L)
                .customerCode("C001")
                .name("テスト")
                .status("ACTIVE")
                .creditLimit(100L)
                .build();
        customer.setCreatedAt(java.time.LocalDateTime.now());
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<CustomerResponse> result = customerService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerCode()).isEqualTo("C001");
    }

    @Test
    void createUsesDefaults() {
        when(customerRepository.existsByCustomerCode("C010")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(99L);
            return c;
        });

        CustomerRequest request = new CustomerRequest();
        request.setCustomerCode("C010");
        request.setName("新規");

        CustomerResponse response = customerService.create(request);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        assertThat(captor.getValue().getCreditLimit()).isEqualTo(0L);
        assertThat(captor.getValue().getStatus()).isEqualTo("ACTIVE");
        assertThat(response.getId()).isEqualTo(99L);
    }

    @Test
    void createThrowsConflictWhenCodeExists() {
        when(customerRepository.existsByCustomerCode("C001")).thenReturn(true);

        CustomerRequest request = new CustomerRequest();
        request.setCustomerCode("C001");
        request.setName("重複");

        assertThatThrownBy(() -> customerService.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void updateThrowsConflictWhenCodeUsedByOther() {
        Customer current = Customer.builder().id(1L).customerCode("C001").name("A").status("ACTIVE").build();
        Customer other = Customer.builder().id(2L).customerCode("C002").name("B").status("ACTIVE").build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(current));
        when(customerRepository.findByCustomerCode("C002")).thenReturn(Optional.of(other));

        CustomerRequest request = new CustomerRequest();
        request.setCustomerCode("C002");
        request.setName("A更新");

        assertThatThrownBy(() -> customerService.update(1L, request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void getEntityThrowsNotFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getEntity(999L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}

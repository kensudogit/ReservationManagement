package com.receivables.service;

import com.receivables.dto.CustomerRequest;
import com.receivables.dto.CustomerResponse;
import com.receivables.entity.Customer;
import com.receivables.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream().map(CustomerResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        return CustomerResponse.from(getEntity(id));
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (customerRepository.existsByCustomerCode(request.getCustomerCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "得意先コードが既に存在します");
        }
        Customer customer = Customer.builder()
                .customerCode(request.getCustomerCode())
                .name(request.getName())
                .contactName(request.getContactName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .creditLimit(request.getCreditLimit() != null ? request.getCreditLimit() : 0L)
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .build();
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = getEntity(id);
        customerRepository.findByCustomerCode(request.getCustomerCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "得意先コードが既に存在します");
                });
        customer.setCustomerCode(request.getCustomerCode());
        customer.setName(request.getName());
        customer.setContactName(request.getContactName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        if (request.getCreditLimit() != null) {
            customer.setCreditLimit(request.getCreditLimit());
        }
        if (request.getStatus() != null) {
            customer.setStatus(request.getStatus());
        }
        return CustomerResponse.from(customerRepository.save(customer));
    }

    public Customer getEntity(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "得意先が見つかりません"));
    }
}

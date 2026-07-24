package com.receivables.service;

import com.receivables.dto.DashboardSummary;
import com.receivables.repository.CustomerRepository;
import com.receivables.repository.ReceivableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final ReceivableRepository receivableRepository;

    @Transactional(readOnly = true)
    public DashboardSummary summary() {
        return DashboardSummary.builder()
                .customerCount(customerRepository.count())
                .openReceivableCount(receivableRepository.countByStatus("OPEN")
                        + receivableRepository.countByStatus("PARTIAL")
                        + receivableRepository.countByStatus("OVERDUE"))
                .overdueCount(receivableRepository.countByStatus("OVERDUE"))
                .totalOpenBalance(receivableRepository.sumOpenBalance())
                .build();
    }
}

package com.receivables.service;

import com.receivables.dto.ReceivableRequest;
import com.receivables.dto.ReceivableResponse;
import com.receivables.entity.Customer;
import com.receivables.entity.Receivable;
import com.receivables.repository.ReceivableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceivableService {

    private final ReceivableRepository receivableRepository;
    private final CustomerService customerService;

    @Transactional(readOnly = true)
    public List<ReceivableResponse> findAll() {
        return receivableRepository.findAll().stream().map(ReceivableResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ReceivableResponse findById(Long id) {
        return ReceivableResponse.from(getEntity(id));
    }

    @Transactional
    public ReceivableResponse create(ReceivableRequest request) {
        if (receivableRepository.findByInvoiceNo(request.getInvoiceNo()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "請求番号が既に存在します");
        }
        Customer customer = customerService.getEntity(request.getCustomerId());
        Receivable receivable = Receivable.builder()
                .invoiceNo(request.getInvoiceNo())
                .customer(customer)
                .invoiceDate(request.getInvoiceDate())
                .dueDate(request.getDueDate())
                .amount(request.getAmount())
                .balance(request.getAmount())
                .currency(request.getCurrency() != null ? request.getCurrency() : "JPY")
                .status(resolveStatus(request.getDueDate(), request.getAmount()))
                .description(request.getDescription())
                .build();
        return ReceivableResponse.from(receivableRepository.save(receivable));
    }

    @Transactional
    public void applyPayment(Receivable receivable, BigDecimal paymentAmount) {
        BigDecimal newBalance = receivable.getBalance().subtract(paymentAmount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "入金額が残高を超えています");
        }
        receivable.setBalance(newBalance);
        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            receivable.setStatus("CLOSED");
        } else if (receivable.getDueDate().isBefore(LocalDate.now())) {
            receivable.setStatus("OVERDUE");
        } else {
            receivable.setStatus("PARTIAL");
        }
        receivableRepository.save(receivable);
    }

    public Receivable getEntity(Long id) {
        return receivableRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "債権が見つかりません"));
    }

    private String resolveStatus(LocalDate dueDate, BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) == 0) {
            return "CLOSED";
        }
        if (dueDate.isBefore(LocalDate.now())) {
            return "OVERDUE";
        }
        return "OPEN";
    }
}

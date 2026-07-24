package com.receivables.repository;

import com.receivables.entity.Receivable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ReceivableRepository extends JpaRepository<Receivable, Long> {
    Optional<Receivable> findByInvoiceNo(String invoiceNo);

    List<Receivable> findByStatusOrderByDueDateAsc(String status);

    List<Receivable> findByCustomerIdOrderByDueDateAsc(Long customerId);

    @Query("select coalesce(sum(r.balance), 0) from Receivable r where r.status <> 'CLOSED'")
    BigDecimal sumOpenBalance();

    long countByStatus(String status);
}

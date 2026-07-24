package com.receivables.repository;

import com.receivables.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByReceivableIdOrderByPaymentDateDesc(Long receivableId);
}

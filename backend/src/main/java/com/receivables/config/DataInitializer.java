package com.receivables.config;

import com.receivables.entity.Customer;
import com.receivables.entity.Receivable;
import com.receivables.repository.CustomerRepository;
import com.receivables.repository.ReceivableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final ReceivableRepository receivableRepository;

    @Override
    public void run(String... args) {
        if (customerRepository.count() > 0) {
            return;
        }

        Customer c1 = customerRepository.save(Customer.builder()
                .customerCode("C001")
                .name("株式会社サンプル商事")
                .contactName("山田太郎")
                .email("yamada@sample.co.jp")
                .phone("03-1234-5678")
                .creditLimit(10_000_000L)
                .status("ACTIVE")
                .build());

        Customer c2 = customerRepository.save(Customer.builder()
                .customerCode("C002")
                .name("合同会社ネクスト物流")
                .contactName("佐藤花子")
                .email("sato@next-logi.jp")
                .phone("06-9876-5432")
                .creditLimit(5_000_000L)
                .status("ACTIVE")
                .build());

        receivableRepository.save(Receivable.builder()
                .invoiceNo("INV-2026-001")
                .customer(c1)
                .invoiceDate(LocalDate.now().minusDays(20))
                .dueDate(LocalDate.now().plusDays(10))
                .amount(new BigDecimal("1250000"))
                .balance(new BigDecimal("1250000"))
                .currency("JPY")
                .status("OPEN")
                .description("6月分請求")
                .build());

        receivableRepository.save(Receivable.builder()
                .invoiceNo("INV-2026-002")
                .customer(c2)
                .invoiceDate(LocalDate.now().minusDays(45))
                .dueDate(LocalDate.now().minusDays(15))
                .amount(new BigDecimal("480000"))
                .balance(new BigDecimal("480000"))
                .currency("JPY")
                .status("OVERDUE")
                .description("延滞債権（サンプル）")
                .build());

        log.info("初期サンプルデータを投入しました");
    }
}

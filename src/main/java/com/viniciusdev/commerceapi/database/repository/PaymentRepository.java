package com.viniciusdev.commerceapi.database.repository;

import com.viniciusdev.commerceapi.database.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}

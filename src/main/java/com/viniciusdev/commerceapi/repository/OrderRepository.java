package com.viniciusdev.commerceapi.repository;

import com.viniciusdev.commerceapi.database.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

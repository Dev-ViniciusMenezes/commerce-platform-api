package com.viniciusdev.commerceapi.database.repository;

import com.viniciusdev.commerceapi.database.model.Order;
import com.viniciusdev.commerceapi.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus (OrderStatus status);
}

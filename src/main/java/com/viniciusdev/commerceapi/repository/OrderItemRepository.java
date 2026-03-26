package com.viniciusdev.commerceapi.repository;


import com.viniciusdev.commerceapi.database.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}

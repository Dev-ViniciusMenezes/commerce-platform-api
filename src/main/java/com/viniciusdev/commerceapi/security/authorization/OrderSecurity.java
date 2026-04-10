package com.viniciusdev.commerceapi.security.authorization;

import com.viniciusdev.commerceapi.database.model.Order;
import com.viniciusdev.commerceapi.database.model.User;
import com.viniciusdev.commerceapi.database.repository.OrderRepository;
import com.viniciusdev.commerceapi.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component ("orderSecurity")
@RequiredArgsConstructor
public class OrderSecurity {

    private final OrderRepository orderRepository;

    public boolean validateOwner(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return order.getClient().getId().equals(userId);
    }
}

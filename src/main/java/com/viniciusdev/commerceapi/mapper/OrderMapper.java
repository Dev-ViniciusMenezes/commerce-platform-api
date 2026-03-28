package com.viniciusdev.commerceapi.mapper;

import com.viniciusdev.commerceapi.database.model.Order;
import com.viniciusdev.commerceapi.database.model.User;
import com.viniciusdev.commerceapi.dto.OrderRequest;
import com.viniciusdev.commerceapi.dto.OrderResponse;
import com.viniciusdev.commerceapi.dto.OrderUpdate;
import com.viniciusdev.commerceapi.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final UserMapper userMapper;
    private final OrderItemMapper orderItemMapper;

    public Order toEntity(OrderRequest request, User user) {
        Order order = new Order(null, Instant.now(), OrderStatus.WAITING_PAYMENT, user, null);
        return order;
    }

    public OrderResponse toDTO(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getMoment(),
                order.getStatus(),
                order.getItems().stream()
                        .map(orderItemMapper::toDTO)
                        .collect(Collectors.toSet()),
                order.getPayment() != null ? order.getPayment().getMoment():  null,
                userMapper.toDTO(order.getClient()),
                order.getTotal()
        );
    }

}

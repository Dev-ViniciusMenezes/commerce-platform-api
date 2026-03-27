package com.viniciusdev.commerceapi.mapper;

import com.viniciusdev.commerceapi.database.model.OrderItem;
import com.viniciusdev.commerceapi.dto.OrderItemResponse;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    public OrderItemResponse toDTO (OrderItem item) {
        return new OrderItemResponse (
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice(),
                item.getSubTotal()
        );
    }
}

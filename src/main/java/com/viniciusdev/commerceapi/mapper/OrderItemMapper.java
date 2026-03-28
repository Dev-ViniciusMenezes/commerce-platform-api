package com.viniciusdev.commerceapi.mapper;

import com.viniciusdev.commerceapi.database.model.OrderItem;
import com.viniciusdev.commerceapi.dto.OrderItemRequest;
import com.viniciusdev.commerceapi.dto.OrderItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderItemMapper {

    public OrderItem toEntity (OrderItemRequest request) {
        OrderItem item = new OrderItem();
        item.setQuantity(request.quantity());
        return item;
    }


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

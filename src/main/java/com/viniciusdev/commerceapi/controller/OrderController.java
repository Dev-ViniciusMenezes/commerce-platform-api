package com.viniciusdev.commerceapi.controller;

import com.viniciusdev.commerceapi.dto.*;
import com.viniciusdev.commerceapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus (HttpStatus.CREATED)
    public OrderResponse createOrder (@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void deleteOrder (@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus (HttpStatus.OK)
    public OrderResponse getOrderById (@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getAllOrders () {
        return orderService.getAllOrders();
    }

    @PatchMapping("/{id}/confirm")
    @ResponseStatus (HttpStatus.OK)
    public OrderResponse confirmPayment (@PathVariable Long id) {
        return orderService.confirmPayment(id);
    }

    @PatchMapping("/{id}/cancel")
    @ResponseStatus (HttpStatus.OK)
    public OrderResponse cancelOrder (@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }


    @PatchMapping("/{id}/confirm-delivery")
    @ResponseStatus (HttpStatus.OK)
    public OrderResponse confirmDelivery (@PathVariable Long id) {
        return orderService.confirmDelivered(id);
    }

    @PostMapping("/{orderId}/items")
    @ResponseStatus (HttpStatus.CREATED)
    public OrderResponse addItemInOrder(@PathVariable Long orderId, @RequestBody OrderItemRequest request) {
        return orderService.addItemInOrder(orderId, request);
    }

    @PutMapping("/{orderId}/items/{productId}")
    @ResponseStatus (HttpStatus.OK)
    public OrderResponse updateItemInOrder(@PathVariable Long orderId,@PathVariable Long productId, @RequestBody OrderItemRequest request) {
        return orderService.updateItemInOrder(orderId, productId, request);
    }

    @DeleteMapping("/{orderId}/items/{productId}")
    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void removeItemInOrder(@PathVariable Long orderId, @PathVariable Long productId) {
        orderService.removeItemInOrder(orderId, productId);
    }
}

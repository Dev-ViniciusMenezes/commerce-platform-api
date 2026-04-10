package com.viniciusdev.commerceapi.controller;

import com.viniciusdev.commerceapi.database.model.User;
import com.viniciusdev.commerceapi.dto.*;
import com.viniciusdev.commerceapi.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Orders", description = "Management for orders")
@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation (summary = "Create a new order")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@AuthenticationPrincipal User user) {
        return orderService.createOrder(user);
    }

    @Operation (summary = "Delete a order")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER') and @orderSecurity.validateOwner(#id, #user.id)")
    public void deleteOrder(@PathVariable Long id, @AuthenticationPrincipal User user) {
        orderService.deleteOrder(id);
    }

    @Operation (summary = "Get a order by id")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @Operation (summary = "Get all orders")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @Operation (summary = "Get all orders of a user")
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public List<OrderResponse> getMyOrders(@AuthenticationPrincipal User user) {
        return orderService.userGetAllOrders(user.getId());
    }

    @Operation (summary = "Pay an order")
    @PatchMapping("/{id}/pay")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER') and @orderSecurity.validateOwner(#id, #user.id)")
    public OrderResponse pay(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return orderService.generatePayment(id);
    }

    @Operation (summary = "Cancel an order")
    @PatchMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }


    @Operation (summary = "Confirm delivery of an order")
    @PatchMapping("/{id}/confirm-delivery")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER') and @orderSecurity.validateOwner(#id, #user.id)")
    public OrderResponse confirmDelivery(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return orderService.confirmDelivered(id);
    }

    @Operation (summary = "Add item in order")
    @PostMapping("/{orderId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER') and @orderSecurity.validateOwner(#orderId, #user.id)")
    public OrderResponse addItemInOrder(@PathVariable Long orderId, @RequestBody @Valid OrderItemRequest request, @AuthenticationPrincipal User user) {
        return orderService.addItemInOrder(orderId, request);
    }

    @Operation (summary = "Update item in order")
    @PutMapping("/{orderId}/items/{productId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole ('USER') and @orderSecurity.validateOwner(#orderId, #user.id)")
    public OrderResponse updateItemInOrder(@PathVariable Long orderId, @PathVariable Long productId, @RequestBody @Valid OrderItemRequest request, @AuthenticationPrincipal User user) {
        return orderService.updateItemInOrder(orderId, productId, request);
    }

    @Operation (summary = "Remove item in order")
    @DeleteMapping("/{orderId}/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER') and @orderSecurity.validateOwner(#orderId, #user.id)")
    public void removeItemInOrder(@PathVariable Long orderId, @PathVariable Long productId, @AuthenticationPrincipal User user) {
        orderService.removeItemInOrder(orderId, productId);
    }
}

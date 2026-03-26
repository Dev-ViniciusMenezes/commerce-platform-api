package com.viniciusdev.commerceapi.service;


import com.viniciusdev.commerceapi.database.model.Order;
import com.viniciusdev.commerceapi.database.model.OrderItem;
import com.viniciusdev.commerceapi.database.model.Product;
import com.viniciusdev.commerceapi.database.model.User;
import com.viniciusdev.commerceapi.dto.OrderItemResponse;
import com.viniciusdev.commerceapi.dto.OrderRequest;
import com.viniciusdev.commerceapi.dto.OrderResponse;
import com.viniciusdev.commerceapi.dto.UserResponse;
import com.viniciusdev.commerceapi.enums.OrderStatus;
import com.viniciusdev.commerceapi.repository.OrderRepository;
import com.viniciusdev.commerceapi.repository.ProductRepository;
import com.viniciusdev.commerceapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderResponse createOrder(OrderRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Order order = new Order(null, Instant.now(), request.status(), user, null);
        orderRepository.save(order);
        return new OrderResponse(
                order.getId(),
                order.getMoment(),
                order.getStatus(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getPrice(),
                                item.getSubTotal()
                        )).collect(Collectors.toSet()),

                null,
                new UserResponse(
                        order.getClient().getId(),
                        order.getClient().getName(),
                        order.getClient().getEmail(),
                        order.getClient().getPhone()
                )
        );
    }

    public OrderResponse updateOrder(Long orderId, OrderRequest request) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(request.status());
        orderRepository.save(order);
        return new OrderResponse(
                order.getId(),
                order.getMoment(),
                order.getStatus(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getPrice(),
                                item.getSubTotal()
                        )).collect(Collectors.toSet()),

                order.getPayment().getMoment() != null ? order.getPayment().getMoment() : Instant.now(),
                new UserResponse(
                        order.getClient().getId(),
                        order.getClient().getName(),
                        order.getClient().getEmail(),
                        order.getClient().getPhone()
                )
        );
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        return new OrderResponse(
                order.getId(),
                order.getMoment(),
                order.getStatus(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getPrice(),
                                item.getSubTotal()
                        )).collect(Collectors.toSet()),

                order.getPayment().getMoment(),
                new UserResponse(
                        order.getClient().getId(),
                        order.getClient().getName(),
                        order.getClient().getEmail(),
                        order.getClient().getPhone()
                )
        );
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getMoment(),
                        order.getStatus(),
                        order.getItems().stream()
                                .map(item -> new OrderItemResponse(
                                        item.getProduct().getId(),
                                        item.getProduct().getName(),
                                        item.getQuantity(),
                                        item.getPrice(),
                                        item.getSubTotal()
                                )).collect(Collectors.toSet()),

                        order.getPayment().getMoment(),
                        new UserResponse(
                                order.getClient().getId(),
                                order.getClient().getName(),
                                order.getClient().getEmail(),
                                order.getClient().getPhone()
                        )
                )).toList();
    }

    public OrderResponse confirmPayment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        paymentService.createPayment(order);
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        return new OrderResponse(
                order.getId(),
                order.getMoment(),
                order.getStatus(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getPrice(),
                                item.getSubTotal()
                        )).collect(Collectors.toSet()),

                order.getPayment().getMoment() != null ? order.getPayment().getMoment() : Instant.now(),
                new UserResponse(
                        order.getClient().getId(),
                        order.getClient().getName(),
                        order.getClient().getEmail(),
                        order.getClient().getPhone()
                )
        );
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void canceledExpiredOrders() {
        int minutesInSeconds = 30 * 60;
        List<Order> expiredOrders = orderRepository.findAll().stream()
                .filter(order -> order.getStatus().equals(OrderStatus.WAITING_PAYMENT))
                .filter(order -> order.getMoment().plusSeconds(minutesInSeconds).isBefore(Instant.now()))
                .peek(order -> order.setStatus(OrderStatus.CANCELED)).toList();
        orderRepository.saveAll(expiredOrders);
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    public OrderResponse addItemInOrder(Long orderId, Long productId, Integer quantity) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        OrderItem existingItem = order.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            OrderItem orderItem = new OrderItem(null, product.getPrice(), quantity, order, product);
            order.addItem(orderItem);
        }
        orderRepository.save(order);
        return new OrderResponse(
                order.getId(),
                order.getMoment(),
                order.getStatus(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getPrice(),
                                item.getSubTotal()
                        )).collect(Collectors.toSet()),

                order.getPayment().getMoment() != null ? order.getPayment().getMoment() : Instant.now(),
                new UserResponse(
                        order.getClient().getId(),
                        order.getClient().getName(),
                        order.getClient().getEmail(),
                        order.getClient().getPhone()
                ));
    }

    public OrderResponse removeItemInOrder(Long orderId, Long productId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        OrderItem orderItem = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found"));
        order.removeItem(orderItem);
        orderRepository.save(order);
        return new OrderResponse(
                order.getId(),
                order.getMoment(),
                order.getStatus(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getPrice(),
                                item.getSubTotal()
                        )).collect(Collectors.toSet()),

                order.getPayment().getMoment() != null ? order.getPayment().getMoment() : Instant.now(),
                new UserResponse(
                        order.getClient().getId(),
                        order.getClient().getName(),
                        order.getClient().getEmail(),
                        order.getClient().getPhone()
                )
        );
    }

    public OrderResponse updateItemInOrder(Long orderId, Long productId, Integer quantity) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        OrderItem orderItem = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found"));

        orderItem.setQuantity(quantity);
        orderRepository.save(order);
        return new OrderResponse(
                order.getId(),
                order.getMoment(),
                order.getStatus(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getPrice(),
                                item.getSubTotal()
                        )).collect(Collectors.toSet()),

                order.getPayment().getMoment() != null ? order.getPayment().getMoment() : Instant.now(),
                new UserResponse(
                        order.getClient().getId(),
                        order.getClient().getName(),
                        order.getClient().getEmail(),
                        order.getClient().getPhone()
                )
        );
    }


}


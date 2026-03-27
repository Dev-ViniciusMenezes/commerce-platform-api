package com.viniciusdev.commerceapi.service;


import com.viniciusdev.commerceapi.database.model.Order;
import com.viniciusdev.commerceapi.database.model.OrderItem;
import com.viniciusdev.commerceapi.database.model.Product;
import com.viniciusdev.commerceapi.database.model.User;
import com.viniciusdev.commerceapi.dto.OrderRequest;
import com.viniciusdev.commerceapi.dto.OrderResponse;
import com.viniciusdev.commerceapi.enums.OrderStatus;
import com.viniciusdev.commerceapi.mapper.OrderItemMapper;
import com.viniciusdev.commerceapi.mapper.OrderMapper;
import com.viniciusdev.commerceapi.database.repository.OrderRepository;
import com.viniciusdev.commerceapi.database.repository.ProductRepository;
import com.viniciusdev.commerceapi.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderResponse createOrder(OrderRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Order order = orderMapper.toEntity(request, user);
        orderRepository.save(order);
        return orderMapper.toDTO(order);
    }

    public OrderResponse updateOrder(Long orderId, OrderRequest request) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        orderMapper.toUpdate(order, request);
        orderRepository.save(order);
        return orderMapper.toDTO(order);
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        return orderMapper.toDTO(order);
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public OrderResponse confirmPayment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        paymentService.createPayment(order);
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        return orderMapper.toDTO(order);
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void canceledExpiredOrders() {
        int expirationInSeconds = 30 * 60;
        List<Order> expiredOrders = orderRepository.findAll().stream()
                .filter(order -> order.getStatus().equals(OrderStatus.WAITING_PAYMENT))
                .filter(order -> order.getMoment().plusSeconds(expirationInSeconds).isBefore(Instant.now()))
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
        return orderMapper.toDTO(order);
    }

    public OrderResponse removeItemInOrder(Long orderId, Long productId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        OrderItem orderItem = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found"));
        order.removeItem(orderItem);
        orderRepository.save(order);
        return orderMapper.toDTO(order);
    }

    public OrderResponse updateItemInOrder(Long orderId, Long productId, Integer quantity) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        OrderItem orderItem = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found"));

        orderItem.setQuantity(quantity);
        orderRepository.save(order);
        return orderMapper.toDTO(order);
    }


}


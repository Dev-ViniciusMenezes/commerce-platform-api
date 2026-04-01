package com.viniciusdev.commerceapi.service;


import com.viniciusdev.commerceapi.database.model.Order;
import com.viniciusdev.commerceapi.database.model.OrderItem;
import com.viniciusdev.commerceapi.database.model.Product;
import com.viniciusdev.commerceapi.database.model.User;
import com.viniciusdev.commerceapi.dto.OrderItemRequest;
import com.viniciusdev.commerceapi.dto.OrderRequest;
import com.viniciusdev.commerceapi.dto.OrderResponse;
import com.viniciusdev.commerceapi.dto.OrderUpdate;
import com.viniciusdev.commerceapi.enums.OrderStatus;
import com.viniciusdev.commerceapi.exception.*;
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


    public OrderResponse createOrder(OrderRequest request) {
        User user = userRepository.findById(request.userId()).orElseThrow(() -> new ResourceNotFoundException("User not found " + request.userId()));
        Order order = orderMapper.toEntity(request, user);
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        orderRepository.save(order);
        return orderMapper.toDTO(order);
    }


    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (order.getStatus() != OrderStatus.WAITING_PAYMENT) {
            throw new OrderStatusException("Cannot delete an order with status: " + order.getStatus());
        }
        orderRepository.delete(order);
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found " + orderId));
        return orderMapper.toDTO(order);
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public OrderResponse confirmPayment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found " + orderId));

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new OrderStatusException("Cannot pay a canceled order");
        }

        if (order.getStatus() == OrderStatus.PAID) {
            throw new OrderStatusException("Order already has a payment");
        }

        if (order.getItems().isEmpty()) {
            throw new EmptyOrderException("Cannot pay an empty order");
        }

        paymentService.createPayment(order);
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        return orderMapper.toDTO(order);
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void cancelExpiredOrders() {
        int expirationInSeconds = 30 * 60;
        List<Order> expiredOrders = orderRepository.findByStatus(OrderStatus.WAITING_PAYMENT);
        expiredOrders.forEach(order -> {
            if (order.getMoment().plusSeconds(expirationInSeconds).isBefore(Instant.now()))
                order.setStatus(OrderStatus.CANCELED);
        });
        orderRepository.saveAll(expiredOrders);
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void shippedOrders() {
        List<Order> shippedOrders = orderRepository.findByStatus(OrderStatus.PAID);
        shippedOrders.forEach(order -> {
            order.setStatus(OrderStatus.SHIPPED);
        });

        orderRepository.saveAll(shippedOrders);
    }

    public OrderResponse confirmDelivered (Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found " + orderId));
        if (order.getStatus() != OrderStatus.SHIPPED){
            throw new OrderStatusException("Order not shipped");
        }

        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
        return orderMapper.toDTO(order);
    }

    public OrderResponse cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found " + orderId));
        if (order.getStatus() == OrderStatus.PAID) {
            throw new OrderStatusException("Cannot cancel a paid order");
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new OrderStatusException("Order already canceled");
        }
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        return orderMapper.toDTO(order);
    }

    public OrderResponse addItemInOrder(Long orderId, OrderItemRequest request) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found " + orderId));
        Product product = productRepository.findById(request.productId()).orElseThrow(() -> new ResourceNotFoundException("Product not found " + request.productId()));

        if (order.getStatus() != OrderStatus.WAITING_PAYMENT) {
            throw new OrderStatusException("Cannot modify an order with status: " + order.getStatus() + "");
        }


        OrderItem existingItem = order.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(request.productId()))
                .findFirst()
                .orElse(null);


        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.quantity());
        } else {
            OrderItem orderItem = orderItemMapper.toEntity(request);
            orderItem.setProduct(product);
            orderItem.setPrice(product.getPrice());
            order.addItem(orderItem);
        }

        orderRepository.save(order);
        return orderMapper.toDTO(order);
    }

    public OrderResponse removeItemInOrder(Long orderId, Long productId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found " + orderId));
        OrderItem orderItem = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found " + productId));
        if (order.getStatus() != OrderStatus.WAITING_PAYMENT) {
            throw new OrderStatusException("Cannot modify an order with status: " + order.getStatus() + "");
        }

        order.removeItem(orderItem);
        orderRepository.save(order);
        return orderMapper.toDTO(order);
    }

    public OrderResponse updateItemInOrder(Long orderId, Long productId, OrderItemRequest request) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found " + orderId));
        OrderItem orderItem = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found " + productId));
        if (order.getStatus() != OrderStatus.WAITING_PAYMENT) {
            throw new OrderStatusException("Cannot modify an order with status: " + order.getStatus() + "");
        }


        orderItem.setQuantity(request.quantity());
        orderRepository.save(order);
        return orderMapper.toDTO(order);
    }


}


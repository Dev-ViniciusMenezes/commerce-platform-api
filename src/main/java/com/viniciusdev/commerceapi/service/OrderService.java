package com.viniciusdev.commerceapi.service;


import com.viniciusdev.commerceapi.database.model.Order;
import com.viniciusdev.commerceapi.database.model.OrderItem;
import com.viniciusdev.commerceapi.database.model.Product;
import com.viniciusdev.commerceapi.database.model.User;
import com.viniciusdev.commerceapi.dto.OrderItemRequest;
import com.viniciusdev.commerceapi.dto.OrderResponse;
import com.viniciusdev.commerceapi.enums.OrderStatus;
import com.viniciusdev.commerceapi.enums.PaymentStatus;
import com.viniciusdev.commerceapi.exception.*;
import com.viniciusdev.commerceapi.mapper.OrderItemMapper;
import com.viniciusdev.commerceapi.mapper.OrderMapper;
import com.viniciusdev.commerceapi.database.repository.OrderRepository;
import com.viniciusdev.commerceapi.database.repository.ProductRepository;
import com.viniciusdev.commerceapi.database.repository.UserRepository;
import jakarta.transaction.Transactional;
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
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;


    @Transactional
    public OrderResponse createOrder(User user) {
        Order order = orderMapper.toEntity(user);
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        orderRepository.save(order);
        return orderMapper.toDTO(order);
    }

    @Transactional
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

    public List<OrderResponse> userGetAllOrders(Long userId) {
        return orderRepository.findAllByClientId(userId)
                .stream()
                .map(orderMapper::toDTO)
                .toList();

    }

    @Transactional
    public OrderResponse generatePayment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found " + orderId));

        if (order.getPayment() != null) {
            throw new PaymentAlreadyExistsException("Payment already exists for this order");
        }

        if (order.getStatus() != OrderStatus.WAITING_PAYMENT) {
            throw new OrderStatusException("Cannot generated a payment for an order with status: " + order.getStatus());
        }

        if (order.getItems().isEmpty()) {
            throw new EmptyOrderException("Cannot pay an empty order");
        }

        paymentService.createPayment(order);
        order.setStatus(OrderStatus.PENDING);
        return orderMapper.toDTO(order);
    }


    @Scheduled(fixedRate = 30 * 1000)
    @Transactional
    public void processApprovedPayments() {
        List<Order> ordersList = orderRepository.findByStatus(OrderStatus.PENDING);


        ordersList.forEach(order -> {

            if (order.getPayment() == null) {
                return;
            }
            if (order.getPayment().getStatus() == PaymentStatus.APPROVED) {
                order.setStatus(OrderStatus.PAID);
            } else if (order.getPayment().getStatus() == PaymentStatus.CANCELED) {
                order.setStatus(OrderStatus.CANCELED);
            }
        });

    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    @Transactional
    public void cancelExpiredOrders() {
        int expirationInSeconds = 30 * 60;
        List<Order> expiredOrders = orderRepository.findByStatus(OrderStatus.WAITING_PAYMENT);
        expiredOrders.forEach(order -> {
            if (order.getMoment().plusSeconds(expirationInSeconds).isBefore(Instant.now()))
                order.setStatus(OrderStatus.CANCELED);
        });
    }


    @Scheduled(fixedRate = 60 * 1000)
    @Transactional
    public void shippedOrders() {
        List<Order> shippedOrders = orderRepository.findByStatus(OrderStatus.PAID);
        shippedOrders.forEach(order -> {
            order.setStatus(OrderStatus.SHIPPED);
        });
    }

    @Transactional
    public OrderResponse confirmDelivered(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found " + orderId));
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new OrderStatusException("Order not shipped");
        }

        order.setStatus(OrderStatus.DELIVERED);
        return orderMapper.toDTO(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found " + orderId));
        if (order.getStatus() == OrderStatus.PAID) {
            throw new OrderStatusException("Cannot cancel a paid order");
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new OrderStatusException("Order already canceled");
        }
        order.setStatus(OrderStatus.CANCELED);


        return orderMapper.toDTO(order);
    }

    @Transactional
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


        return orderMapper.toDTO(order);
    }

    @Transactional
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
        return orderMapper.toDTO(order);
    }


    @Transactional
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
        return orderMapper.toDTO(order);
    }


}


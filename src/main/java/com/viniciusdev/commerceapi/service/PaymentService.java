package com.viniciusdev.commerceapi.service;


import com.viniciusdev.commerceapi.database.model.Order;
import com.viniciusdev.commerceapi.database.model.Payment;
import com.viniciusdev.commerceapi.dto.PaymentResponse;
import com.viniciusdev.commerceapi.repository.OrderRepository;
import com.viniciusdev.commerceapi.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;


    public PaymentResponse createPayment(Order order) {
        Payment payment = new Payment(null, Instant.now(), order);
        order.setPayment(payment);
        paymentRepository.save(payment);
        return new PaymentResponse(payment.getId(), payment.getMoment(), order.getStatus());
    }


    public PaymentResponse findPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
        return new PaymentResponse(payment.getId(), payment.getMoment(), payment.getOrder().getStatus());
    }

    public List<PaymentResponse> findAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(payment -> new PaymentResponse(payment.getId(), payment.getMoment(), payment.getOrder().getStatus()))
                .toList();

    }

    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
        paymentRepository.delete(payment);
    }

}


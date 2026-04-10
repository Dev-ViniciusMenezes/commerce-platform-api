package com.viniciusdev.commerceapi.service;


import com.viniciusdev.commerceapi.database.model.Order;
import com.viniciusdev.commerceapi.database.model.Payment;
import com.viniciusdev.commerceapi.dto.PaymentResponse;
import com.viniciusdev.commerceapi.enums.PaymentStatus;
import com.viniciusdev.commerceapi.exception.PaymentAlreadyExistsException;
import com.viniciusdev.commerceapi.exception.ResourceNotFoundException;
import com.viniciusdev.commerceapi.mapper.PaymentMapper;
import com.viniciusdev.commerceapi.database.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;


    @Transactional
    public PaymentResponse createPayment(Order order) {
        Payment payment = new Payment(null, Instant.now(), PaymentStatus.WAITING_PAYMENT, order);
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setOrder(order);
        order.setPayment(payment);
        paymentRepository.save(payment);
        return paymentMapper.toDTO(payment);
    }


    public PaymentResponse findPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment not found " + id));
        return paymentMapper.toDTO(payment);
    }

    public List<PaymentResponse> findAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(paymentMapper::toDTO)
                .toList();
    }


}


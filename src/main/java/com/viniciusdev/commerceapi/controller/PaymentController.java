package com.viniciusdev.commerceapi.controller;


import com.viniciusdev.commerceapi.database.model.Order;
import com.viniciusdev.commerceapi.database.model.Payment;
import com.viniciusdev.commerceapi.dto.PaymentResponse;
import com.viniciusdev.commerceapi.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponse findPaymentById(@PathVariable Long id) {
        return paymentService.findPaymentById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PaymentResponse> findAllPayments() {
        return paymentService.findAllPayments();
    }


}

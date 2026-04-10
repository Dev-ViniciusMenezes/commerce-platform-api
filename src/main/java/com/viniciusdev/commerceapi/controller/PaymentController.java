package com.viniciusdev.commerceapi.controller;


import com.viniciusdev.commerceapi.database.model.Order;
import com.viniciusdev.commerceapi.database.model.Payment;
import com.viniciusdev.commerceapi.dto.PaymentResponse;
import com.viniciusdev.commerceapi.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Payments", description = "Management for payments")
@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation (summary = "Get payment by id")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponse findPaymentById(@PathVariable Long id) {
        return paymentService.findPaymentById(id);
    }

    @Operation (summary = "Get all payments")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PaymentResponse> findAllPayments() {
        return paymentService.findAllPayments();
    }


}

package com.viniciusdev.commerceapi.mapper;

import com.viniciusdev.commerceapi.database.model.Payment;
import com.viniciusdev.commerceapi.dto.PaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toDTO (Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getMoment(),
                payment.getOrder().getStatus()
        );
    }
}

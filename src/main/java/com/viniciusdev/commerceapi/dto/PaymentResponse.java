package com.viniciusdev.commerceapi.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.viniciusdev.commerceapi.enums.OrderStatus;

import java.time.Instant;

public record PaymentResponse(
        Long id,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant moment,
        OrderStatus status
){
}

package com.viniciusdev.commerceapi.dto;

public record UserRequest (
        String name,
        String email,
        String phone,
        String password
) {
}

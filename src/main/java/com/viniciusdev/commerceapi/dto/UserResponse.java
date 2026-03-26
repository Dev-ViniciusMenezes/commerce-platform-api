package com.viniciusdev.commerceapi.dto;

public record UserResponse (
        Long id,
        String name,
        String email,
        String phone
){
}

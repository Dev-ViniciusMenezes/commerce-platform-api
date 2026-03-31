package com.viniciusdev.commerceapi.dto;


import jakarta.validation.constraints.NotBlank;

public record CategoryRequest (
        @NotBlank (message = "Name must not be a blank")
        String name
) {
}

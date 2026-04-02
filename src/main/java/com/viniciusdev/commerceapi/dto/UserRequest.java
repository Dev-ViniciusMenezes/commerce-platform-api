package com.viniciusdev.commerceapi.dto;

import jakarta.validation.constraints.*;

public record UserRequest(

        @NotBlank (message = "Name must not be blank")
        @Size(min = 3, max = 250, message = "Name must be between 3 and 250 characters")
        String name,

        @NotBlank (message = "Email must not be blank")
        @Email (message = "Email must be valid")
        String email,

        @Pattern(
                regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}",
                message = "Phone must be in the format (XX) XXXXX-XXXX"
        )
        String phone,

        @NotBlank (message = "Password must not be blank")
        @Size (min = 8, message = "Password must be at least 8 characters")
        String password
) {
}

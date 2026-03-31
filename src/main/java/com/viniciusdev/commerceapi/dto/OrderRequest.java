package com.viniciusdev.commerceapi.dto;


import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @NotNull (message = "User id must not be null")
       Long userId
){
}


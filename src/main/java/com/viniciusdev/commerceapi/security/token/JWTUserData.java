package com.viniciusdev.commerceapi.security.token;

import lombok.Builder;

@Builder
public record JWTUserData (Long userId, String email){
}

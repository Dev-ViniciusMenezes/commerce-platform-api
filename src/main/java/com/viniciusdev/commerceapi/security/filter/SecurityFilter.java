package com.viniciusdev.commerceapi.security.filter;


import com.viniciusdev.commerceapi.database.model.User;
import com.viniciusdev.commerceapi.database.repository.UserRepository;
import com.viniciusdev.commerceapi.exception.ResourceNotFoundException;
import com.viniciusdev.commerceapi.security.token.TokenService;
import com.viniciusdev.commerceapi.security.token.JWTUserData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());
            Optional <JWTUserData> optUser = tokenService.validateToken(token);
            if (optUser.isPresent()) {
                JWTUserData userData = optUser.get();
                User user = userRepository.findById(userData.userId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}

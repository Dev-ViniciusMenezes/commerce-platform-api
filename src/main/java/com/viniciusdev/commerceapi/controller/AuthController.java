package com.viniciusdev.commerceapi.controller;

import com.viniciusdev.commerceapi.security.token.TokenService;
import com.viniciusdev.commerceapi.database.model.User;
import com.viniciusdev.commerceapi.database.repository.UserRepository;
import com.viniciusdev.commerceapi.dto.LoginRequest;
import com.viniciusdev.commerceapi.dto.LoginResponse;
import com.viniciusdev.commerceapi.dto.UserRequest;
import com.viniciusdev.commerceapi.dto.UserResponse;
import com.viniciusdev.commerceapi.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "Management for authentication")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;


    @Operation (summary = "Login")
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken userPass = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(userPass);

        User user = (User) authentication.getPrincipal();
        String token = tokenService.generatedToken(user);
        return new LoginResponse(token);
    }


    @Operation (summary = "Register")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody UserRequest request) {
        User user = userMapper.toEntity(request);
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

}

package com.viniciusdev.commerceapi.controller;

import com.viniciusdev.commerceapi.database.model.User;
import com.viniciusdev.commerceapi.dto.UserRequest;
import com.viniciusdev.commerceapi.dto.UserResponse;
import com.viniciusdev.commerceapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "Management for users")
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @Operation (summary = "Get all users")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @Operation (summary = "Get a user by id")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @Operation (summary = "Update a user")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER') and @userSecurity.isOwner( #id, principal)")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody @Valid UserRequest request) {
        return userService.update(id, request);
    }

    @Operation (summary = "Delete a user")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER') and @userSecurity.isOwner( #id, principal)")
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }
}

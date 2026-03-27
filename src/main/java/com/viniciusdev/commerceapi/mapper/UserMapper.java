package com.viniciusdev.commerceapi.mapper;

import com.viniciusdev.commerceapi.database.model.User;
import com.viniciusdev.commerceapi.dto.UserRequest;
import com.viniciusdev.commerceapi.dto.UserResponse;
import com.viniciusdev.commerceapi.database.repository.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final UserRepository repository;

    public User toEntity (UserRequest request) {
        User user = new User(null , request.name(),request.email(),request.phone(), request.password());
        return user;
    }

    public UserResponse toDTO (User user) {
      return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );
    }

    public User toUpdate (User user , UserRequest request) {
        if (request.name() != null) {
            user.setName(request.name());
        }

        if (request.email() != null) {
             user.setEmail(request.email());
         }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        if (request.password()!= null) {
            user.setPassword(request.password());
        }
        return user;
    }
}

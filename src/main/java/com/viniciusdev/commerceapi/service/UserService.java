package com.viniciusdev.commerceapi.service;
import com.viniciusdev.commerceapi.database.model.User;
import com.viniciusdev.commerceapi.dto.UserRequest;
import com.viniciusdev.commerceapi.dto.UserResponse;
import com.viniciusdev.commerceapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhone()
                )).toList();
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );
    }

    public UserResponse create(UserRequest userRequest) {
        User user = new User(null, userRequest.name(), userRequest.email(), userRequest.phone(), userRequest.password());
        userRepository.save(user);
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone());

    }

    public UserResponse update(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(userRequest.name());
        user.setEmail(userRequest.email());
        user.setPhone(userRequest.phone());
        userRepository.save(user);
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone());
    }


    public void delete(Long id) {
        userRepository.deleteById(id);
    }

}


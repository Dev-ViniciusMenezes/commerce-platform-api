package com.viniciusdev.commerceapi.service;

import com.viniciusdev.commerceapi.database.model.User;
import com.viniciusdev.commerceapi.dto.UserRequest;
import com.viniciusdev.commerceapi.dto.UserResponse;
import com.viniciusdev.commerceapi.mapper.UserMapper;
import com.viniciusdev.commerceapi.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::toDTO)
                .toList();
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDTO(user);
    }

    public UserResponse create(UserRequest userRequest) {
        User user = userMapper.toEntity(userRequest);
        userRepository.save(user);
        return userMapper.toDTO(user);
    }


    public UserResponse update(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.toUpdate(user, userRequest);
        userRepository.save(user);
        return userMapper.toDTO(user);
    }


    public void delete(Long id) {
        userRepository.deleteById(id);
    }

}


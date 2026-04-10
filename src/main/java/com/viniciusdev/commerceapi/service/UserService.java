package com.viniciusdev.commerceapi.service;

import com.viniciusdev.commerceapi.database.model.User;
import com.viniciusdev.commerceapi.dto.UserRequest;
import com.viniciusdev.commerceapi.dto.UserResponse;
import com.viniciusdev.commerceapi.exception.ResourceNotFoundException;
import com.viniciusdev.commerceapi.mapper.UserMapper;
import com.viniciusdev.commerceapi.database.repository.UserRepository;
import jakarta.transaction.Transactional;
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
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found " + id));
        return userMapper.toDTO(user);
    }



    @Transactional
    public UserResponse update(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found " + id));
        userMapper.toUpdate(user, userRequest);
        return userMapper.toDTO(user);
    }


    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }

}


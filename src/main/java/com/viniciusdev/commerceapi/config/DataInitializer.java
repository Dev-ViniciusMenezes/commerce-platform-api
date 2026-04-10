package com.viniciusdev.commerceapi.config;

import com.viniciusdev.commerceapi.database.model.User;
import com.viniciusdev.commerceapi.database.repository.UserRepository;
import com.viniciusdev.commerceapi.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            if (userRepository.findUserByEmail("admin2@email.com").isEmpty()) {
                User admin = new User();
                admin.setName("Admin2");
                admin.setEmail("admin2@email.com");
                admin.setPhone("(71) 99999-9911");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setRole(UserRole.ADMIN);
                userRepository.save(admin);

                System.out.println("ADMIN CRIADO!");
            }
        };
    }
}
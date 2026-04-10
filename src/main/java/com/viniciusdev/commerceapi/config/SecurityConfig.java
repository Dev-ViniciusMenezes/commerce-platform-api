package com.viniciusdev.commerceapi.config;


import com.viniciusdev.commerceapi.security.filter.SecurityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()


                        .requestMatchers(HttpMethod.POST, "/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/categories/**").permitAll()

                        //CATEGORY
                        .requestMatchers(HttpMethod.POST, "/v1/categories").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/v1/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v1/categories/**").hasRole("ADMIN")

                        //PRODUCT
                        .requestMatchers(HttpMethod.POST, "/v1/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/v1/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v1/products/**").hasRole("ADMIN")

                        //PAYMENT
                        .requestMatchers(HttpMethod.GET, "/v1/payments").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/v1/payments/**").hasRole("ADMIN")

                        //ORDER
                        .requestMatchers(HttpMethod.PATCH, "/v1/orders/{id}/cancel").hasRole("ADMIN")

                        //USER
                        .requestMatchers(HttpMethod.GET, "/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/v1/users/**").hasRole("ADMIN")

                        .anyRequest().authenticated()

                ).addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

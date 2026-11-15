package com.markethub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Allow all web requests so your custom controllers and Thymeleaf templates handle routing and login
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                // Disable CSRF temporarily for smooth local form testing and custom controller handling
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
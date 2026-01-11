package com.healthtracker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // ОБЯЗАТЕЛЬНО
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // Отключаем CSRF для POST-запросов
        .cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(List.of("http://localhost:4200"));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(List.of("*"));
            return config;
        }))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/h2-console/**").permitAll() // РАЗРЕШАЕМ ДОСТУП К H2
            .anyRequest().permitAll()
        )
        // ЭТО НУЖНО, ЧТОБЫ H2 ОТКРЫВАЛСЯ ВНУТРИ БРАУЗЕРА (ФРЕЙМЫ)
        .headers(headers -> headers.frameOptions(frame -> frame.disable())); 
        
    return http.build();
}
}
package com.djg_bank.djg_bank.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().permitAll() // Allow all requests
                )
                .csrf(csrf -> csrf.disable()) // Disable CSRF
                .formLogin(Customizer.withDefaults()) // Disable form login
                .httpBasic(Customizer.withDefaults()); // Disable basic auth

        return http.build();
    }
}

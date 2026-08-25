package com.sharep.global.config;

import com.sharep.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(scrf -> scrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/user/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/").authenticated()
                        .requestMatchers(HttpMethod.GET, "/{prompt-id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/?sort_by=latest").authenticated()
                        .requestMatchers(HttpMethod.GET, "/?sort_by=popularity").authenticated()
                        .requestMatchers(HttpMethod.GET, "/?search=값").authenticated()
                        .requestMatchers(HttpMethod.POST, "/filter").authenticated()


                        .anyRequest().permitAll()
                )
                .addFilterBefore(
                        jwtTokenFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }
}
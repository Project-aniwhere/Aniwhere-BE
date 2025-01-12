package com.example.aniwhere.application.config.security;

import com.example.aniwhere.application.auth.handler.CustomAccessDeniedHandler;
import com.example.aniwhere.application.auth.handler.CustomAuthenticationEntryPoint;
import com.example.aniwhere.application.auth.jwt.filter.JwtTokenFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/favicon.ico"),
								new AntPathRequestMatcher("/api/auth/kakao/callback"),
								new AntPathRequestMatcher("/api/auth/email"),
								new AntPathRequestMatcher("/api/auth/email/verifications-requests"),
								new AntPathRequestMatcher("/api/auth/kakao/login"),
								new AntPathRequestMatcher("/login/**"),
                                new AntPathRequestMatcher("/api/login"),
                                new AntPathRequestMatcher("/api/token"),
                                new AntPathRequestMatcher("/api/auth/**"),
								new AntPathRequestMatcher("/api/anime/**"),
								new AntPathRequestMatcher("/api/episodes/**"),
                                new AntPathRequestMatcher("/recommend"),
                                new AntPathRequestMatcher("/anime/*"),
								new AntPathRequestMatcher("/api/v3/api-docs/**"),
								new AntPathRequestMatcher("/api/swagger-ui/**")
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ConditionalAuthFilter extends OncePerRequestFilter {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Check if the request is to the /api/v1/auth/** endpoints;
        if (request.getRequestURI().startsWith("/api/v1/auth/") || request.getRequestURI().startsWith("/token/refresh")) {
            // If so, proceed without applying the JWTAuthenticationFilter
            filterChain.doFilter(request, response);
        } else {
            // Otherwise, delegate to the JWTAuthenticationFilter
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        }
    }
}
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
        String uri = request.getRequestURI();
        if ("/graphql/schema".equals(uri)) {
            filterChain.doFilter(request, response);
        } else if (isWhiteListed(uri)) {
            filterChain.doFilter(request, response);
        } else {
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        }
    }
    private boolean isWhiteListed(String uri) {
        return uri.startsWith("/api/v1/auth/") || uri.startsWith("/token/refresh");
    }

}
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "https://taskminder-b-end-production.up.railway.app", "https://taskminder-b-end-production.up.railway.app/landing", "https://taskminder-b-end-production.up.railway.app/dashboard","https://taskminder-b-end-production.up.railway.app/tasks", "https://taskminder-b-end-production.up.railway.app/boards", "https://taskminder-b-end-production.up.railway.app/profile")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

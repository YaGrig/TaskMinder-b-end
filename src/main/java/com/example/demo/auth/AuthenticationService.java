package com.example.demo.auth;

import com.example.demo.config.JwtService;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    public final UserRepository repository;
    public final PasswordEncoder encoder;
    public final JwtService jwtService;
    public final AuthenticationManager authenticationManager;
    private final HttpServletRequest request;
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder().firstname(request.getFirstname()).lastname(request.getLastname()).email(request.getEmail()).password(encoder.encode(request.getPassword())).role(Role.valueOf(Role.USER.name())).build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user, user.getId());
        var refreshToken = jwtService.generateRefreshToken(user.getId());
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user, user.getId());
        var refreshToken = jwtService.generateRefreshToken(user.getId());

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
    public AuthenticationResponse refreshAccessToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("Refresh-Token");
        UUID userId = jwtService.getUserIdFromRequest(request);
        if (jwtService.isRefreshTokenValid(refreshToken, userId)) {
            Optional<User> userOptional = repository.findById(userId);
            if( userOptional.isPresent()) {
                User user = userOptional.get();
                String newAccessToken = jwtService.generateToken(user, userId);

                return AuthenticationResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        } else {
            // Если refresh token недействителен, отправляем ошибку
            return AuthenticationResponse.builder()
                    .accessToken("")
                    .refreshToken("")
                    .build();
        }
        return AuthenticationResponse.builder()
                .accessToken("")
                .refreshToken("")
                .build();
    }
}

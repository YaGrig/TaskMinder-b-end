package com.example.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Secure key generation

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getId);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(UserDetails userDetails, UUID userId){
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return generateToken(claims, userDetails);
    }

    public String generateRefreshToken(UUID userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        // Устанавливаем более длительный срок жизни для refresh token
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + + 1000L * 60 * 60 * 24 * 30)) // Например, 30 дней
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isRefreshTokenValid(String token, UUID userId) {
        final String subject = extractClaim(token, Claims::getSubject);

        return (subject.equals(userId.toString())) && !isTokenExpired(token);
    }

    private UUID extractUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String userIdString = claims.get("userId", String.class);
            // Log the userIdString value
            // Convert the String to UUID
            return UUID.fromString(userIdString);
        } catch (IllegalArgumentException e) {
            // Handle the case where the string is not a valid UUID
            System.err.println("Error: userIdString is not a valid UUID.");
            return null;
        }
    }

    public UUID getUserIdFromRequest(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            return extractUserIdFromToken(token);
        }
        return null; // В случае, если токен не найден или невалиден
    }

    public static String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        return null; // Token not found or doesn't start with "Bearer "
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000 * 10)) // Adjusted to 10 hours
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
//+ 1000 * 60 * 60 * 10
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
         extractExpiration(token).before(new Date());
        return false;
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}

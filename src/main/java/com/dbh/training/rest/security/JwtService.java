package com.dbh.training.rest.security;

import com.dbh.training.rest.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.List;
import javax.inject.Singleton;

/**
 * Service for JWT token generation and validation
 * 
 * Exercise 08: Security Implementation
 * Handles JWT token operations using JJWT library
 */
@Singleton
public class JwtService {
    
    // In production, this should come from environment variable
    private static final String SECRET_ENV = System.getenv("JWT_SECRET");
    private static final String DEFAULT_SECRET = "training-secret-key-minimum-256-bits-for-hs256-algorithm";
    private static final String SECRET = SECRET_ENV != null ? SECRET_ENV : DEFAULT_SECRET;
    
    // Token expiration time (1 hour)
    private static final long EXPIRATION_TIME = 3600000;
    
    private final Key key;
    
    public JwtService() {
        // Create key from secret
        this.key = Keys.hmacShaKeyFor(SECRET.getBytes());
    }
    
    /**
     * Generate JWT token for authenticated user
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_TIME);
        
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("userId", user.getId())
            .claim("roles", user.getRoles())
            .claim("email", user.getEmail())
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }
    
    /**
     * Validate and parse JWT token
     * @throws io.jsonwebtoken.JwtException if token is invalid
     */
    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
    
    /**
     * Extract username from token
     */
    public String getUsername(String token) {
        return validateToken(token).getSubject();
    }
    
    /**
     * Extract user ID from token
     */
    public Long getUserId(String token) {
        return validateToken(token).get("userId", Long.class);
    }
    
    /**
     * Extract roles from token
     */
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        return validateToken(token).get("roles", List.class);
    }
    
    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = validateToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
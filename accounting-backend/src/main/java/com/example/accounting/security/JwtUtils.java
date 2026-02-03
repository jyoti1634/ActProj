package com.example.accounting.security;

// Utility class for JWT operations like generation and validation
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
// for generating secret keys
import io.jsonwebtoken.security.Keys;
// to inject values from application properties
import org.springframework.beans.factory.annotation.Value;
// to define this class as a Spring component
import org.springframework.stereotype.Component;

// for secret key representation
import javax.crypto.SecretKey;
// for handling dates
import java.util.Date;

// Defines this class as a Spring component
@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(prefix = "app.security", name = "enabled", havingValue = "true", matchIfMissing = true)
public class JwtUtils {
    // Secret key for signing JWTs
    private final SecretKey jwtSecretKey;
    // JWT expiration time in milliseconds
    private final long jwtExpirationMs;
    // Constructor to initialize secret key and expiration time from application properties
    public JwtUtils(@Value("${app.security.jwtSecret}") String jwtSecret,
                    @Value("${app.security.jwtExpirationMs}") long jwtExpirationMs) {
        this.jwtExpirationMs = jwtExpirationMs;
        try {
            // validate key strength and create SecretKey
            this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        } catch (io.jsonwebtoken.security.WeakKeyException e) {
            int bitLen = jwtSecret == null ? 0 : jwtSecret.getBytes().length * 8;
            throw new IllegalStateException("JWT secret is too weak (" + bitLen + " bits). " +
                    "Set `app.security.jwtSecret` to at least 256 bits (32 bytes). Example: `openssl rand -hex 32` produces 64 hex chars.", e);
        }
    }
    // Generates a JWT token for the given subject (e.g., username)
    public String generateToken(String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    // Parses the JWT token and returns the claims
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    // Validates the JWT token
    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

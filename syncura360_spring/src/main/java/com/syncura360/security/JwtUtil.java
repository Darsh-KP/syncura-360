package com.syncura360.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.crypto.SecretKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final SecretKey key;

    public JwtUtil(@Value("${JWT_SECRET_KEY}") String secret) {
        byte [] decodedKey = Base64Coder.decode(secret);
        key = Keys.hmacShaKeyFor(decodedKey);
    }

    public String getRole(String authHeader) {

        String jwt = authHeader.substring(7);
        Jws<Claims> jwsClaims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt);
        Claims claims = jwsClaims.getPayload();
        return claims.get("role", String.class);

    }

    public String generateJwtToken(String username, String role) {

        long currentMillis = System.currentTimeMillis();
        Date currentTime = new Date(currentMillis);

        return Jwts.builder()
                .subject(username)
                .issuedAt(currentTime)
                .expiration(new Date(currentMillis + 3600000))
                .claim("role", role)
                .signWith(key)
                .compact();
    }

}

package com.syncura360.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import javax.crypto.SecretKey;
import java.util.Date;

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

    public String getUsername(String authHeader) {

        String jwt = authHeader.substring(7);
        Jws<Claims> jwsClaims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt);
        Claims claims = jwsClaims.getPayload();
        return claims.get("sub", String.class);

    }

    public String getHospitalID(String authHeader) {
        String jwt = authHeader.substring(7);
        Jws<Claims> jwsClaims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt);
        Claims claims = jwsClaims.getPayload();
        return claims.get("hospitalID", String.class);
    }

    public String generateJwtToken(String username, String role, String hospitalID) {

        long currentMillis = System.currentTimeMillis();
        Date currentTime = new Date(currentMillis);

        return Jwts.builder()
                .subject(username)
                .issuedAt(currentTime)
                .expiration(new Date(currentMillis + 3600000))
                .claim("role", role)
                .claim("hospitalID", hospitalID)
                .signWith(key)
                .compact();
    }

}

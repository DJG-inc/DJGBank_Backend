package com.djg_bank.djg_bank.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private final Long jwtExpirationMs = 3600000L;

    private String jwtSecret = "NX6RScZzYvduC8YktGexQ74FksuPa6h0xVdnvRzOztA=";

    private final Key jwtSecretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());

    public String generateJwtToken(Long id) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(id.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey)
                .compact();
    }

    public Long getIdFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(authToken);
            System.out.println("Token válido");
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Token inválido");
        }
        return false;
    }
}

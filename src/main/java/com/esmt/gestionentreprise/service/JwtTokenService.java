package com.esmt.gestionentreprise.service;

import com.esmt.gestionentreprise.utils.SecParams;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Singleton
public class JwtTokenService {
    @EJB
    private EmployeeService employeeService;

    private static final String SECRET_KEY = SecParams.SECRET;

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUsername(String token) {
        return extractClaim(token, claims -> (String) claims.get("username"));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> (String) claims.get("role"));
    }

    public String generateToken(String email, long userId) {
        String username = employeeService.getNomEtPrenomByEmail(email);
        String role = employeeService.getRoleByEmail(email);
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", userId);
        extraClaims.put("username", username);
        extraClaims.put("role", role);
        return generateToken(extraClaims, email);
    }

    private String generateToken(Map<String, Object> extraClaims, String email) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + SecParams.EXP_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return extractedEmail.equals(email) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims decodeToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("Invalid key: Must be at least 32 bytes");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

package com.esmt.gestionentreprise.utils;

import com.esmt.gestionentreprise.service.JwtTokenService;
import io.jsonwebtoken.Claims;

public class JwtTokenServiceTest {
    public static void main(String[] args) {
        // Création d'une instance de JwtTokenService
        JwtTokenService jwtService = new JwtTokenService();

        // Données de test
        String email = "user@example.com";
        long userId = 1;

        // Génération du token
        String token = jwtService.generateToken(email, userId);
        System.out.println("Token généré : " + token);

        // Vérification de la validité du token
        boolean isValid = jwtService.isTokenValid(token, email);
        System.out.println("Le token est-il valide ? " + isValid);

        // Extraction de l'email du token
        String extractedEmail = jwtService.extractEmail(token);
        System.out.println("Email extrait du token : " + extractedEmail);

        // Vérification de l'expiration du token
        boolean isExpired = jwtService.isTokenExpired(token);
        System.out.println("Le token est-il expiré ? " + isExpired);

        // Extraction des claims
        Claims claims = jwtService.extractAllClaims(token);
        System.out.println("Claims du token : " + claims);
    }
}

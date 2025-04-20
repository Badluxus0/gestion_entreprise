package com.esmt.gestionentreprise.service;

import com.esmt.gestionentreprise.utils.SecParams;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class JwtTokenService {

    private static final Logger logger = Logger.getLogger(JwtTokenService.class.getName());
    private static final String SECRET_KEY = SecParams.SECRET;
    private static final long EXPIRATION_TIME = SecParams.EXP_TIME;

    @EJB
    private EmployeeService employeeService;

    /**
     * Extrait l'email à partir du token JWT (subject)
     * @param token Le token JWT
     * @return L'email extrait
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait le nom d'utilisateur à partir du token JWT
     * @param token Le token JWT
     * @return Le nom d'utilisateur
     */
    public String extractUsername(String token) {
        return extractClaim(token, claims -> (String) claims.get("username"));
    }

    /**
     * Extrait le rôle à partir du token JWT
     * @param token Le token JWT
     * @return Le rôle
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> (String) claims.get("role"));
    }

    /**
     * Génère un token JWT pour l'utilisateur spécifié
     * @param email L'email de l'utilisateur
     * @param userId L'ID de l'utilisateur
     * @return Le token JWT généré
     */
    public String generateToken(String email, long userId) {
        try {
            String username = employeeService.getNomEtPrenomByEmail(email);
            String role = employeeService.getRoleByEmail(email);

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", userId);
            extraClaims.put("username", username);
            extraClaims.put("role", role);

            return generateToken(extraClaims, email);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la génération du token: {0}", e.getMessage());
            throw e;
        }
    }

    /**
     * Génère un token JWT avec des claims supplémentaires
     * @param extraClaims Les claims supplémentaires
     * @param email L'email de l'utilisateur (subject)
     * @return Le token JWT généré
     */
    private String generateToken(Map<String, Object> extraClaims, String email) {
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Vérifie si le token est valide pour l'email spécifié
     * @param token Le token JWT
     * @param email L'email à vérifier
     * @return true si le token est valide, false sinon
     */
    public boolean isTokenValid(String token, String email) {
        try {
            final String extractedEmail = extractEmail(token);
            return extractedEmail.equals(email) && !isTokenExpired(token);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Validation du token échouée: {0}", e.getMessage());
            return false;
        }
    }

    /**
     * Vérifie si le token est expiré
     * @param token Le token JWT
     * @return true si le token est expiré, false sinon
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Erreur lors de la vérification de l'expiration: {0}", e.getMessage());
            return true;
        }
    }

    /**
     * Extrait la date d'expiration du token
     * @param token Le token JWT
     * @return La date d'expiration
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait une claim spécifique du token JWT
     * @param token Le token JWT
     * @param claimsResolver La fonction pour extraire la claim
     * @return La claim extraite
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait toutes les claims du token
     * @param token Le token JWT
     * @return Les claims
     */
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.log(Level.WARNING, "Token JWT expiré: {0}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            logger.log(Level.SEVERE, "Token JWT invalide: {0}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Token JWT vide ou null: {0}", e.getMessage());
            throw e;
        }
    }

    // Utilisez cette méthode si votre SECRET_KEY est une chaîne ordinaire (non encodée)
    private Key getSigningKeyFromPlainText() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Récupère la clé de signature
     * @return La clé de signature
     */
    private Key getSigningKey() {
        try {
            return getSigningKeyFromPlainText();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la création de la clé de signature: {0}", e.getMessage());
            throw e;
        }
    }
}
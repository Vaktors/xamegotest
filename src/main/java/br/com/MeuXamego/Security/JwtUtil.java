package br.com.MeuXamego.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    /**
     * Chave secreta do JWT. Pode ser plain-text ou Base64.
     * Para HS256, recomenda-se ao menos 32 bytes (256 bits).
     */
    private final String secret;

    /**
     * Expiração (em segundos).
     */
    private final long expirationSeconds;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-seconds}") long expirationSeconds
    ) {
        this.secret = secret;
        this.expirationSeconds = expirationSeconds;
    }

    /**
     * Gera um token JWT usando o e-mail como "subject".
     */
    public String generateToken(String email) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + (expirationSeconds * 1000));

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Retorna a expiração configurada (em segundos).
     */
    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    /**
     * Obtém o e-mail (subject) de um token.
     */
    public String getUsernameFromToken(String token) {
        return getAllClaims(token).getSubject();
    }

    /**
     * Valida assinatura e expiração do token.
     */
    public boolean isValid(String token) {
        try {
            getAllClaims(token); // dispara exceção se assinatura/expiração inválidas
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Retorna a Data de expiração do token.
     */
    public Date getExpirationDateFromToken(String token) {
        return getAllClaims(token).getExpiration();
    }

    /**
     * Verifica se já expirou.
     */
    public boolean isTokenExpired(String token) {
        Date exp = getExpirationDateFromToken(token);
        return exp.before(new Date());
    }

    // ------------ Helpers ------------

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        // Tenta decodificar como Base64; se falhar, usa bytes em UTF-8.
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException ignore) {
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }
}


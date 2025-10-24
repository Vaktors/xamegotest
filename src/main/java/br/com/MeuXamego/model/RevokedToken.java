package br.com.MeuXamego.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "revoked_tokens")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class RevokedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String token;

    @Column(name = "revoked_at", nullable = false)
    private LocalDateTime revokedAt = LocalDateTime.now();

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt = LocalDateTime.now().plusHours(2); // adiciona validade padrão

    public RevokedToken() {}

    public RevokedToken(String token, LocalDateTime expiresAt) {
        this.token = token;
        this.revokedAt = LocalDateTime.now();
        this.expiresAt = expiresAt != null ? expiresAt : LocalDateTime.now().plusHours(2);
    }

    // getters e setters
}
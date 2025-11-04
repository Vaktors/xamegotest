package br.com.MeuXamego.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "revoked_tokens") // bate com V4
@Getter @Setter                // gera getters/setters
@NoArgsConstructor             // construtor vazio
@AllArgsConstructor            // construtor com todos os campos
@Builder                       // [ADICIONADO] agora temos .builder()
public class RevokedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                   // BIGINT AUTO_INCREMENT na V4

    @Column(nullable = false, unique = true, length = 600)
    private String token;                              // JWT

    @Column(name = "revoked_at", nullable = false)
    private LocalDateTime revokedAt;                   // quando fez logout

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;                   // quando esse token expiraria normalmente

    @Column(name = "revoked_after")
    private Instant revokedAfter;        // marco de revogação (cut-off)
}

package br.com.MeuXamego.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmailVerification {                         //

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;                                  

    @ManyToOne(optional = false, fetch = FetchType.LAZY) // vários códigos para um usuário
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;                             // FK para Usuario

    @Column(name = "codigo", length = 5, nullable = false)
    private String codigo;                               // 5 dígitos

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;                      //

    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;                      // +15 min

    @Column(name = "tentativas_invalidas", nullable = false)
    private Integer tentativasInvalidas = 0;             // 

    @Column(name = "reenviados", nullable = false)
    private Integer reenviados = 0;                      //

    @Column(name = "usado", nullable = false)
    private boolean usado = false;                       //
}

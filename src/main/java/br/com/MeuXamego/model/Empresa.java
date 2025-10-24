package br.com.MeuXamego.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Empresa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEmpresa")
    private Long id;

    @OneToOne
    @JoinColumn(name = "idUsuario", referencedColumnName = "idUsuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "nomeOng", nullable = false)
    private String nomeOng;

    @Column(name = "cnpj", nullable = false, length = 20)
    private String cnpj;
}

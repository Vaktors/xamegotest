package br.com.MeuXamego.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "PessoaFis")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PessoaFis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPessoaFis")
    private Long id;

    @OneToOne
    @JoinColumn(name = "idUsuario", referencedColumnName = "idUsuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "cpf", nullable = false, length = 11, unique = true)
    private String cpf;

    @Column(name = "sexo", length = 1) // M/F
    private String sexo;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;
}

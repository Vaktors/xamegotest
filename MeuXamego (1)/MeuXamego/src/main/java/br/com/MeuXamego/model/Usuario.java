package br.com.MeuXamego.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Usuario")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    private Long id;

    @Column(name = "nome", nullable = false)        private String nome;
    @Column(name = "email", nullable = false, unique = true) private String email;
    @Column(name = "senha_hash", nullable = false)  private String senhaHash;
    @Column(name = "telefone")                      private String telefone;
    @Column(name = "rua")                           private String rua;
    @Column(name = "numero")                        private String numero;
    @Column(name = "cidade")                        private String cidade;
    @Column(name = "estado", length = 2)            private String estado;
    @Column(name = "cep")                           private String cep;
    @Column(name = "sexo")                          private String sexo;
    @Column(name = "funcao")                        private String funcao;

    @Column(name = "aprovado", nullable = false)    private boolean aprovado = false;
    @Column(name = "ativo", nullable = false)       private boolean ativo = true;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "idUsuario"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "usuario", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private PessoaFis pessoaFis;

    @OneToOne(mappedBy = "usuario", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Empresa empresa;

    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) dataCriacao = LocalDateTime.now();
    }
}

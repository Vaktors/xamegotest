package br.com.MeuXamego.model; // pacote da entidade

import jakarta.persistence.*;   // anotações JPA/Hibernate
import lombok.*;                // gera getters/setters/construtores
import java.time.LocalDateTime; // datas sem timezone

@Entity                                           // indica que essa classe é uma tabela
@Table(name = "Usuario")                          // nome da tabela no banco
@Getter @Setter                                   // Lombok gera get*/set*
@NoArgsConstructor @AllArgsConstructor            // Lombok gera construtores
public class Usuario {

    @Id                                           // chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    @Column(name = "idUsuario")                   // coluna real no banco
    private Integer id;                           // [ALTERADO] era Long/BIGINT, agora Integer/INT

    @Column(name = "nome", nullable = false)      // obrigatório
    private String nome;

    @Column(name = "email", nullable = false, unique = true) // único pra login
    private String email;

    @Column(name = "senha_hash", nullable = false)           // hash seguro da senha
    private String senhaHash;

    // ===== Endereço (DF + entorno) =====
    @Enumerated(EnumType.STRING)
    @Column(name = "bairro", length = 50)         // [ADICIONADO] armazena ex.: 'ASA_SUL'
    private Bairro bairro;                        // [ADICIONADO] enum Bairro

    @Column(name = "endereco_detalhes")           // [ADICIONADO] livre: quadra/bloco/lote/complemento
    private String enderecoDetalhes;              // [ADICIONADO] substitui rua/numero/cidade/estado/cep

    // ===== Sexo =====
    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", length = 15)           // 'FEMININO','MASCULINO','OUTROS'
    private Sexo sexo;

    @Column(name = "foto_perfil_url")
    private String fotoPerfilUrl;
    
    @Column(name = "telefone")
    private String telefone;

    // ===== Papel (uma role por usuário) =====
    @ManyToOne(fetch = FetchType.EAGER)           // [ADICIONADO] vários usuários podem ter a mesma role
    @JoinColumn(
        name = "role_id",                         // [ADICIONADO] FK em Usuario
        referencedColumnName = "id",              // referência pra roles.id
        nullable = false                          // [ADICIONADO] todo usuário precisa ter role
    )
    private Role role;                            // [ADICIONADO] substitui Set<Role>, usuario_roles etc.

    @Column(name = "ativo", nullable = false)
    private boolean ativo = false;                // [ALTERADO] começa falso -> só ativa depois do código de verificação

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now(); // timestamp cadastro

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;            // auditoria de login

    // Segurança de login
    @Column(name = "tentativas_falhas", nullable = false) // [ADICIONADO] contador de erro de senha
    private Integer tentativasFalhas = 0;                  // [ADICIONADO]

    @Column(name = "bloqueado_ate")                        // [ADICIONADO] se estiver bloqueado por brute force
    private LocalDateTime bloqueadoAte;                    // [ADICIONADO]

    // Ligações 1:1 adicionais
    @OneToOne(mappedBy = "usuario", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private PessoaFis pessoaFis;                  // dados extras se for PROTETOR

    @OneToOne(mappedBy = "usuario", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Empresa empresa;                      // dados extras se for ONG

    @PrePersist                                   // roda antes do INSERT
    public void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();    // garante não-nulo no insert
        }
        if (email != null) {
            email = email.trim().toLowerCase();   // [ADICIONADO] normaliza e-mail para minúsculo
        }
    }

    @PreUpdate                                    // roda antes do UPDATE
    public void preUpdate() {
        if (email != null) {
            email = email.trim().toLowerCase();   // [ADICIONADO] mantém e-mail sempre em lowercase
        }
    }

    public LocalDateTime getCriadoEm() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCriadoEm'");
    }
}

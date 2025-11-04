-- V1: Modelo inicial com tabelas principais

-- Tabela Usuario
CREATE TABLE IF NOT EXISTS Usuario (
  idUsuario         INT NOT NULL AUTO_INCREMENT,         -- PK INT (não BIGINT) [ALTERADO]
  nome              VARCHAR(255) NOT NULL,               -- nome do usuário
  email             VARCHAR(255) NOT NULL UNIQUE,        -- único por usuário
  senha_hash        VARCHAR(255) NOT NULL,               -- senha criptografada

  bairro            VARCHAR(50) NULL,                    -- [ADICIONADO] enum Bairro em forma de texto
  endereco_detalhes VARCHAR(255) NULL,                   -- [ADICIONADO] complemento livre

  foto_perfil_url varchar(512), 

  sexo              VARCHAR(15) NULL,                    -- 'FEMININO','MASCULINO','OUTROS'

  role_id           INT NOT NULL,                        -- [ADICIONADO] FK direta pra roles.id
                                                         -- regra: 1 usuário = 1 role

  ativo             TINYINT(1) NOT NULL DEFAULT 0,       -- [ALTERADO] começa desativado (ativa após e-mail)
  data_criacao      DATETIME(6) NOT NULL,                -- LocalDateTime.now()
  ultimo_login      DATETIME(6) NULL,                    -- última vez logado

  tentativas_falhas INT NOT NULL DEFAULT 0,              -- [ADICIONADO] segurança login
  bloqueado_ate     DATETIME(6) NULL,                    -- [ADICIONADO] até quando o login está bloqueado

  PRIMARY KEY (idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabela roles
CREATE TABLE IF NOT EXISTS roles (
  id   INT NOT NULL AUTO_INCREMENT,                      -- PK INT (não BIGINT) [ALTERADO]
  nome VARCHAR(50) NOT NULL UNIQUE,                      -- 'ROLE_ADMIN','ROLE_USUARIO','ROLE_ONG','ROLE_PROTETOR'
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- FK Usuario.role_id -> roles.id
ALTER TABLE Usuario
  ADD CONSTRAINT fk_usuario_role
  FOREIGN KEY (role_id) REFERENCES roles(id)
  ON DELETE RESTRICT;                                   -- evita apagar role em uso

-- Tabela Empresa (dados extras de ONG)
CREATE TABLE IF NOT EXISTS Empresa (
  idEmpresa INT NOT NULL AUTO_INCREMENT,                 -- PK INT [ALTERADO]
  idUsuario INT UNIQUE,                                  -- relação 1:1 com Usuario
  nomeOng   VARCHAR(255) NOT NULL,
  cnpj      VARCHAR(14)  NOT NULL,                       -- string, não usar INT em CNPJ
  PRIMARY KEY (idEmpresa),
  CONSTRAINT fk_empresa_usuario
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
    ON DELETE CASCADE                                   -- se deletar o usuário, apaga ONG
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabela PessoaFis (dados extras de protetor)
CREATE TABLE IF NOT EXISTS PessoaFis (
  idPessoaFis   INT NOT NULL AUTO_INCREMENT,             -- PK INT [ALTERADO]
  idUsuario     INT UNIQUE,                              -- relação 1:1 com Usuario
  cpf           VARCHAR(11) NOT NULL UNIQUE,             -- string, não INT
  sexo          VARCHAR(20),                             -- pode manter aqui por agora
  data_nascimento DATE,
  PRIMARY KEY (idPessoaFis),
  CONSTRAINT fk_pf_usuario
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
    ON DELETE CASCADE                                   -- se deletar o usuário, apaga PF
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

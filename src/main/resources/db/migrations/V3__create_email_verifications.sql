-- V3: tabela para verificar e-mail do usuário no cadastro (RF01.2)

CREATE TABLE IF NOT EXISTS email_verifications (
    id INT NOT NULL AUTO_INCREMENT,               -- PK
    usuario_id INT NOT NULL,                      -- FK -> Usuario.idUsuario
    codigo VARCHAR(5) NOT NULL,                   -- código de 5 dígitos enviado pro e-mail
    criado_em DATETIME(6) NOT NULL,               -- quando o código foi gerado
    expira_em DATETIME(6) NOT NULL,               -- até quando vale (ex.: +15 min)
    tentativas_invalidas INT NOT NULL DEFAULT 0,  -- tentativas erradas do usuário
    reenviados INT NOT NULL DEFAULT 0,            -- quantas vezes reenviamos este código
    usado BIT NOT NULL DEFAULT 0,                 -- 1 = já usado (validou), 0 = ainda não

    PRIMARY KEY (id),

    CONSTRAINT fk_ev_usuario
        FOREIGN KEY (usuario_id) REFERENCES Usuario(idUsuario)
        ON DELETE CASCADE,

    -- índices auxiliares para busca/limpeza
    KEY idx_ev_usuario        (usuario_id),
    KEY idx_ev_expira_em      (expira_em),
    KEY idx_ev_usado          (usado)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

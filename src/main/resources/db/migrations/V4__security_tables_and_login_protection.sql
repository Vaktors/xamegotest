-- V4: segurança de sessão (logout/token inválido) e manutenção

-- 1) Tabela de tokens revogados (JWT que não podem mais autenticar)
CREATE TABLE IF NOT EXISTS revoked_tokens (
  id BIGINT NOT NULL AUTO_INCREMENT,              -- pode continuar BIGINT aqui, sem problema
  token VARCHAR(600) NOT NULL,                    -- JWT completo
  expires_at DATETIME(6) NOT NULL,                -- quando esse token naturalmente expiraria
  PRIMARY KEY (id),
  UNIQUE KEY uk_revoked_token (token),            -- não permitir token duplicado
  KEY idx_expires_at (expires_at)                 -- index pra limpeza rápida
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2) Evento opcional para limpar tokens expirados periodicamente
-- OBS: isso só roda se o EVENT SCHEDULER do MySQL estiver ligado (em produção você liga)
DROP EVENT IF EXISTS ev_purge_revoked_tokens;
CREATE EVENT ev_purge_revoked_tokens
    ON SCHEDULE EVERY 1 HOUR
    DO
      DELETE FROM revoked_tokens WHERE expires_at < NOW(6);

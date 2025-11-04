-- V2: Popular tabela de roles com valores padrão


INSERT INTO roles (nome) VALUES ('ROLE_USUARIO')
  ON DUPLICATE KEY UPDATE nome = VALUES(nome);


INSERT INTO roles (nome) VALUES ('ROLE_PROTETOR')
  ON DUPLICATE KEY UPDATE nome = VALUES(nome);
  
  INSERT INTO roles (nome) VALUES ('ROLE_ONG')
  ON DUPLICATE KEY UPDATE nome = VALUES(nome);

INSERT INTO roles (nome) VALUES ('ROLE_ADMIN')
  ON DUPLICATE KEY UPDATE nome = VALUES(nome);
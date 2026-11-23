-- Etapa 2: autenticação e cadastro de tatuadores (RF-01, RF-02, RF-03).

-- Nome de exibição do usuário autenticado (cliente, tatuador ou administrador).
ALTER TABLE usuario ADD COLUMN nome VARCHAR(100);
UPDATE usuario SET nome = email WHERE nome IS NULL;
ALTER TABLE usuario ALTER COLUMN nome SET NOT NULL;

-- Registro profissional do tatuador (RF-03); quando informado, é único.
ALTER TABLE tatuador ADD COLUMN registro_profissional VARCHAR(30);
CREATE UNIQUE INDEX ux_tatuador_registro
    ON tatuador (registro_profissional)
    WHERE registro_profissional IS NOT NULL;

-- [RN-03]: cada sala exclusiva pertence a, no máximo, um tatuador ativo.
CREATE UNIQUE INDEX ux_tatuador_sala_ativo
    ON tatuador (id_sala_padrao)
    WHERE ativo;

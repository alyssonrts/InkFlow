-- Usuários do sistema (RF-01, RF-02): login por e-mail e perfil de acesso.
-- Cada usuário CLIENTE aponta para um cliente, cada TATUADOR para um tatuador;
-- o perfil ADMIN (Recepção/Administrador) não possui vínculo.
CREATE TABLE usuario (
    id_usuario   SERIAL PRIMARY KEY,
    email        VARCHAR(150) UNIQUE NOT NULL,
    senha_hash   VARCHAR(100) NOT NULL,
    perfil       VARCHAR(20)  NOT NULL CHECK (perfil IN ('CLIENTE', 'TATUADOR', 'ADMIN')),
    ativo        BOOLEAN      NOT NULL DEFAULT TRUE,
    id_cliente   INT UNIQUE REFERENCES cliente(id_cliente),
    id_tatuador  INT UNIQUE REFERENCES tatuador(id_tatuador),
    CHECK (
        (perfil = 'CLIENTE'  AND id_cliente IS NOT NULL AND id_tatuador IS NULL) OR
        (perfil = 'TATUADOR' AND id_tatuador IS NOT NULL AND id_cliente IS NULL) OR
        (perfil = 'ADMIN'    AND id_cliente IS NULL AND id_tatuador IS NULL)
    )
);

-- Parâmetros de negócio configuráveis pelo administrador ([RN-04], [RN-09], [RN-14]).
CREATE TABLE parametro_negocio (
    chave      VARCHAR(60)  PRIMARY KEY,
    valor      INT          NOT NULL CHECK (valor > 0),
    descricao  VARCHAR(255) NOT NULL
);

INSERT INTO parametro_negocio (chave, valor, descricao) VALUES
    ('PRAZO_CICATRIZACAO_DIAS', 21, 'Intervalo mínimo, em dias corridos, entre sessões do mesmo projeto [RN-04]'),
    ('PRAZO_GARANTIA_DIAS', 90, 'Prazo de garantia de retoque, em dias corridos, a partir da conclusão do projeto [RN-09]'),
    ('ANTECEDENCIA_CANCELAMENTO_HORAS', 24, 'Antecedência mínima, em horas, para cancelamento de sessão pelo cliente [RN-14]');

-- Histórico de transições de estado do projeto, para auditoria ([NF-09]).
CREATE TABLE historico_status_projeto (
    id_historico    SERIAL PRIMARY KEY,
    id_projeto      INT         NOT NULL REFERENCES projeto_tatuagem(id_projeto),
    status_anterior VARCHAR(30),
    status_novo     VARCHAR(30) NOT NULL,
    id_usuario      INT REFERENCES usuario(id_usuario),
    data_hora       TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_historico_projeto ON historico_status_projeto (id_projeto, data_hora);

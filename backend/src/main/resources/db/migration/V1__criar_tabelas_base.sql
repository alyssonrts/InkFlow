CREATE TABLE sala (
    id_sala      SERIAL PRIMARY KEY,
    nome         VARCHAR(60)  NOT NULL,
    tipo         VARCHAR(20)  NOT NULL DEFAULT 'EXCLUSIVA'
                 CHECK (tipo IN ('EXCLUSIVA','COMPARTILHADA')),
    descricao    VARCHAR(255),
    ativa        BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE tatuador (
    id_tatuador   SERIAL PRIMARY KEY,
    nome          VARCHAR(100) NOT NULL,
    especialidade VARCHAR(80),
    id_sala_padrao INT NOT NULL REFERENCES sala(id_sala),
    ativo         BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE cliente (
    id_cliente         SERIAL PRIMARY KEY,
    nome               VARCHAR(100) NOT NULL,
    cpf                VARCHAR(14)  UNIQUE NOT NULL,
    email              VARCHAR(150) UNIQUE NOT NULL,
    telefone           VARCHAR(20)  NOT NULL,
    data_nascimento    DATE NOT NULL,
    autoriza_portfolio BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE projeto_tatuagem (
    id_projeto       SERIAL PRIMARY KEY,
    id_cliente       INT NOT NULL REFERENCES cliente(id_cliente),
    id_tatuador      INT NOT NULL REFERENCES tatuador(id_tatuador),
    descricao        VARCHAR(255) NOT NULL,
    regiao_corpo     VARCHAR(60)  NOT NULL,
    status           VARCHAR(30)  NOT NULL DEFAULT 'ORCAMENTO_SOLICITADO',
    data_abertura    TIMESTAMP    NOT NULL DEFAULT NOW(),
    data_conclusao   TIMESTAMP,
    data_lib_proxima DATE
);

CREATE TABLE orcamento (
    id_orcamento      SERIAL PRIMARY KEY,
    id_projeto        INT UNIQUE NOT NULL REFERENCES projeto_tatuagem(id_projeto),
    valor_total       DECIMAL(10,2) NOT NULL,
    sessoes_estimadas INT NOT NULL CHECK (sessoes_estimadas > 0),
    aprovado_em       TIMESTAMP
);

CREATE TABLE sessao (
    id_sessao        SERIAL PRIMARY KEY,
    id_projeto       INT NOT NULL REFERENCES projeto_tatuagem(id_projeto),
    id_sala          INT NOT NULL REFERENCES sala(id_sala),
    data_hora_inicio TIMESTAMP NOT NULL,
    data_hora_fim    TIMESTAMP NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'AGENDADA',
    is_retoque       BOOLEAN NOT NULL DEFAULT FALSE,
    observacoes      TEXT,
    CHECK (data_hora_fim > data_hora_inicio)
);

CREATE TABLE material (
    id_material    SERIAL PRIMARY KEY,
    nome           VARCHAR(100) NOT NULL,
    unidade        VARCHAR(20)  NOT NULL,
    estoque_minimo INT NOT NULL DEFAULT 0
);

CREATE TABLE lote_material (
    id_lote       SERIAL PRIMARY KEY,
    id_material   INT NOT NULL REFERENCES material(id_material),
    codigo_lote   VARCHAR(50) NOT NULL,
    quantidade    INT NOT NULL CHECK (quantidade >= 0),
    data_validade DATE NOT NULL,
    UNIQUE (id_material, codigo_lote)
);

CREATE TABLE item_material_sessao (
    id_item    SERIAL PRIMARY KEY,
    id_sessao  INT NOT NULL REFERENCES sessao(id_sessao),
    id_lote    INT NOT NULL REFERENCES lote_material(id_lote),
    quantidade INT NOT NULL CHECK (quantidade > 0)
);

CREATE TABLE garantia (
    id_garantia  SERIAL PRIMARY KEY,
    id_projeto   INT UNIQUE NOT NULL REFERENCES projeto_tatuagem(id_projeto),
    data_inicio  DATE NOT NULL,
    data_limite  DATE NOT NULL
);

-- Índices de apoio à verificação de disponibilidade cruzada [RN-02]
CREATE INDEX idx_sessao_sala_periodo ON sessao (id_sala, data_hora_inicio, data_hora_fim);
CREATE INDEX idx_sessao_projeto ON sessao (id_projeto);

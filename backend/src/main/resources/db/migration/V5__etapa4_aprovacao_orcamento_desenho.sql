
-- ETAPA 4 - APROVACAO DE ORCAMENTO E DESENHO

-- Decisao do cliente sobre o valor
ALTER TABLE orcamento
ADD COLUMN valor_aprovado BOOLEAN;

ALTER TABLE orcamento
ADD COLUMN data_decisao_valor TIMESTAMP;

-- Decisao do cliente sobre o desenho
ALTER TABLE orcamento
ADD COLUMN desenho_aprovado BOOLEAN;

ALTER TABLE orcamento
ADD COLUMN data_decisao_desenho TIMESTAMP;

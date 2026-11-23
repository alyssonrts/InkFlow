-- Etapa 3: a solicitação ainda não é um orçamento emitido.
ALTER TABLE projeto_tatuagem ADD COLUMN tamanho_aproximado VARCHAR(60);
ALTER TABLE projeto_tatuagem ADD COLUMN imagem_referencia_url VARCHAR(1000);
ALTER TABLE orcamento ADD COLUMN duracao_sessao_minutos INTEGER CHECK (duracao_sessao_minutos > 0);
CREATE INDEX idx_projeto_cliente_status ON projeto_tatuagem(id_cliente, status);
CREATE INDEX idx_projeto_tatuador_status ON projeto_tatuagem(id_tatuador, status);

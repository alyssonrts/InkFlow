
-- ETAPA 4 - DESENHO FINAL DO TATUADOR

-- Armazena o endereco do desenho apresentado
-- pelo tatuador para aprovacao do cliente.

ALTER TABLE projeto_tatuagem
ADD COLUMN desenho_final_url VARCHAR(1000);

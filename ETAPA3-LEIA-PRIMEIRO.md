# InkFlow — Etapa 3: solicitação e emissão de orçamento

## O que foi acrescentado
- Cliente: formulário de solicitação de orçamento, escolha de tatuador ativo e lista de pedidos.
- Tatuador: fila de pedidos vinculados ao seu cadastro, emissão de valor, sessões estimadas e duração prevista.
- API: `GET /api/v1/orcamentos`, `POST /api/v1/orcamentos` e `POST /api/v1/orcamentos/{id}/emitir`.
- Flyway: migração V4, preservando as tabelas e os dados existentes.
- Segurança: cliente só vê seus pedidos; tatuador só vê os seus; emissão é restrita ao tatuador responsável.

## Limitações desta etapa
- A imagem de referência é um **link HTTP(S)**, ainda não há upload de arquivos.
- A aprovação do orçamento e do desenho pelo cliente será implementada na Etapa 4. O status `DESENHO_EM_APROVACAO` após emissão não significa aprovação.
- A aplicação ainda não foi validada em execução completa neste ambiente. Teste backend e frontend localmente antes de publicar.

## Como aplicar à versão local
1. Faça backup da pasta InkFlow e do banco PostgreSQL.
2. Copie as pastas `backend/src` e `frontend/src` deste pacote para as mesmas pastas no projeto local, substituindo arquivos quando solicitado.
3. Inicie o PostgreSQL como já fazia: `docker compose up -d` na raiz.
4. Reinicie o backend: `cd backend` e `mvn spring-boot:run` (Flyway aplicará V4).
5. Reinicie o frontend: `cd frontend` e `npm run dev`.
6. Entre com cliente, solicite orçamento; depois entre com tatuador e emita a proposta; retorne ao cliente e veja o valor.

**Importante:** Não envie `.env`, tokens, dados reais de clientes ou contas de demonstração para um servidor público. Não publique esta versão como produção sem testes e ajustes de segurança.

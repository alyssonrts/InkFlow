# InkFlow

**Sistema de Gestão de Tatuagens** - aplicação web para estúdios de tatuagem de pequeno e médio porte.

Projeto desenvolvido na disciplina de Projeto de Desenvolvimento de Sistemas Web (PDSW), do curso de Análise e Desenvolvimento de Sistemas da AESA-CESA (Centro de Ensino Superior de Arcoverde).

> **Status:** em desenvolvimento, por etapas. **Etapa 1:** base do backend e do banco de dados. **Etapa 2:** autenticação (login, token JWT e autocadastro de cliente), controle de acesso por perfil e cadastro de tatuadores pelo administrador. As demais funcionalidades serão adicionadas nas próximas etapas.

## Sobre o projeto

O InkFlow controla, de forma integrada, o fluxo completo de uma tatuagem: **orçamento → aprovação do desenho → agendamento → sessões → cicatrização → conclusão → garantia de retoque**. O sistema:

- valida a disponibilidade cruzada entre tatuador e sala no momento do agendamento;
- controla automaticamente o prazo de cicatrização, bloqueando e liberando a agenda;
- dá baixa automática no estoque de materiais esterilizados a cada sessão realizada;
- separa o acesso por perfil: **Cliente**, **Tatuador** e **Recepção/Administrador**;
- publica trabalhos no portfólio somente com a autorização do cliente (LGPD).

## Tecnologias

| Camada | Tecnologias |
|---|---|
| Frontend | React 18, React Router 6, Axios, Tailwind CSS 3, Vite 5 |
| Backend | Java 21, Spring Boot 3.5.16 (Web, Data JPA, Security, Validation, Actuator), Flyway |
| Banco de dados | PostgreSQL 16 (em contêiner Docker) |
| Testes | JUnit 5 e Mockito |

## Arquitetura

Arquitetura cliente-servidor desacoplada: o frontend e o backend são projetos independentes que se comunicam apenas por uma API REST (JSON). O backend é um monólito em camadas: `controller` (apresentação), `service` (aplicação), `domain` (regras de negócio) e `repository` (acesso a dados).

## Estrutura de pastas

```
InkFlow/
├── backend/
│   ├── src/main/java/br/edu/aesa/inkflow/
│   │   ├── controller/      → endpoints REST e tratamento de requisições
│   │   ├── service/         → casos de uso e orquestração de transações
│   │   ├── domain/          → entidades, enums e regras de negócio
│   │   │   ├── model/
│   │   │   └── exception/
│   │   ├── repository/      → interfaces de acesso a dados (Spring Data)
│   │   ├── dto/             → objetos de transferência de entrada e saída
│   │   └── config/          → segurança, CORS e configurações gerais
│   ├── src/main/resources/
│   │   ├── application.yml  → parâmetros da aplicação
│   │   └── db/migration/    → scripts de criação e evolução da base
│   └── src/test/java/       → testes unitários e de integração
├── frontend/
│   ├── src/
│   │   ├── components/      → componentes reutilizáveis de interface
│   │   ├── pages/           → telas por perfil de usuário
│   │   ├── services/        → consumo da API via Axios
│   │   ├── contexts/        → contexto de autenticação e perfil
│   │   └── routes/          → definição das rotas protegidas
│   └── public/
├── docker-compose.yml
└── README.md
```

## Pré-requisitos

- JDK 21
- Maven 3.9 ou superior (ou uma IDE com suporte a Maven, como o IntelliJ IDEA)
- Node.js 20 LTS
- Docker e Docker Compose
- Git

## Como executar

1. **Clone o repositório**

   ```bash
   git clone https://github.com/alyssonrts/InkFlow.git
   cd InkFlow
   ```

2. **Crie o arquivo de variáveis de ambiente** a partir do modelo:

   ```bash
   cp .env.example .env
   ```

   No Windows (PowerShell): `Copy-Item .env.example .env`. Depois, edite o `.env` e troque a senha do banco (`POSTGRES_PASSWORD`) e a chave dos tokens (`JWT_SECRET`, com pelo menos 32 caracteres). O arquivo `.env` não é versionado.

3. **Suba o banco de dados PostgreSQL**

   ```bash
   docker compose up -d
   ```

4. **Inicie o backend** (as migrações do Flyway criam e atualizam as tabelas, e as contas de demonstração são criadas na primeira execução):

   ```bash
   cd backend
   mvn spring-boot:run
   ```

   Para conferir, acesse <http://localhost:8080/actuator/health>; a resposta esperada é `{"status":"UP"}`.

5. **Inicie o frontend**, em outro terminal:

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

   A interface fica disponível em <http://localhost:5173>. Entre com uma das contas de demonstração abaixo.

## Contas de demonstração

Criadas automaticamente na primeira execução do backend (variável `INKFLOW_DEMONSTRACAO=true`, o padrão do `.env.example`), junto com quatro salas (três exclusivas e uma compartilhada). Existem apenas para avaliação: em qualquer uso real, defina `INKFLOW_DEMONSTRACAO=false` e remova as contas.

| Perfil | Usuário | Senha |
|---|---|---|
| Cliente | cliente@inkflow.com | cliente123 |
| Tatuador | tatuador@inkflow.com | tatuador123 |
| Administrador | admin@inkflow.com | admin123 |

## API

Todas as respostas seguem o envelope `{ "data": ..., "message": "...", "error": ... }`. Os endpoints protegidos exigem o cabeçalho `Authorization: Bearer <token>`, obtido no login.

| Verbo | Endpoint | Acesso | Descrição |
|---|---|---|---|
| POST | `/api/v1/auth/login` | público | Autentica o usuário e retorna o token de acesso |
| POST | `/api/v1/auth/registrar` | público | Autocadastro de cliente |
| GET | `/api/v1/auth/me` | autenticado | Dados do usuário logado |
| GET | `/api/v1/tatuadores` | autenticado | Lista os tatuadores |
| GET | `/api/v1/tatuadores/{id}` | autenticado | Consulta um tatuador |
| POST | `/api/v1/tatuadores` | ADMIN | Cadastra um tatuador e o seu acesso |
| PUT | `/api/v1/tatuadores/{id}` | ADMIN | Atualiza os dados de um tatuador |
| PATCH | `/api/v1/tatuadores/{id}/situacao` | ADMIN | Ativa ou inativa um tatuador |
| GET | `/api/v1/salas` | ADMIN | Lista as salas e indica as ocupadas |

Sem token, a API responde `401`; com um perfil sem permissão, responde `403`; regras de negócio violadas retornam `422`.

Endpoints previstos para as próximas etapas:

| Verbo | Endpoint | Descrição |
|---|---|---|
| POST | `/api/v1/orcamentos` | Registra uma solicitação de orçamento |
| PATCH | `/api/v1/projetos/{id}/aprovar` | Registra a aprovação de orçamento e desenho |
| GET | `/api/v1/agenda/disponibilidade` | Consulta horários livres (tatuador e sala) |
| POST | `/api/v1/sessoes` | Agenda uma nova sessão |
| PATCH | `/api/v1/sessoes/{id}/realizar` | Registra a sessão e dá baixa no estoque |
| POST | `/api/v1/projetos/{id}/retoque` | Solicita retoque, verificando o prazo de garantia |
| GET | `/api/v1/estoque/criticos` | Lista materiais abaixo do nível mínimo |

## Testes

Os testes automatizados do backend (JUnit 5 e Mockito) cobrem a validação de CPF, a emissão de tokens, o login, a regra da sala exclusiva ([RN-03]) e as respostas 401 e 403:

```bash
cd backend
mvn test
```

## Convenções

- **Commits:** mensagens claras e padronizadas, com prefixo de tipo (`feat:`, `fix:`, `docs:`, `chore:`).
- **Versionamento:** versionamento semântico, com tags no Git (por exemplo, `v1.0.0`).
- **Segredos:** senhas e chaves ficam no `.env`, que não é enviado ao repositório.

## Documentação

O Relatório Técnico do projeto (escopo, requisitos, modelagem, interfaces e documentação técnica) acompanha a entrega da disciplina.

## Autoria

- **Autor:** Alysson Araújo Da Silva
- **Orientador:** Profº Dennys Cavalcanti Carvalho
- **Instituição:** AESA-CESA — Autarquia de Ensino Superior de Arcoverde / Centro de Ensino Superior de Arcoverde
- **Curso:** Análise e Desenvolvimento de Sistemas — 4º período, 2026

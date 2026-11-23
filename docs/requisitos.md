# Requisitos do InkFlow

Extraídos do Relatório Técnico (seção 2 – ERS). Prioridades: Essencial, Importante, Desejável.

## Requisitos funcionais

### [RF-01] Cadastro de Usuários

O sistema deve permitir o cadastro de novos usuários com os perfis "Cliente", "Tatuador" ou "Recepção/Administrador".

**Atores:** Cliente, Tatuador e Administrador.

**Prioridade:** Essencial

- Critério de aceitação: O usuário preenche os campos obrigatórios (nome, e-mail, senha e telefone) e recebe confirmação de criação de conta.

### [RF-02] Autenticação e Controle de Acesso

O sistema deve autenticar o usuário por e-mail e senha e liberar apenas as telas e operações permitidas ao seu perfil.

**Atores:** Cliente, Tatuador e Administrador.

**Prioridade:** Essencial

- Critério de aceitação: Após o login, o usuário visualiza somente o menu correspondente ao seu perfil; tentativas de acesso direto a rotas não autorizadas retornam erro 403.

### [RF-03] Cadastro de Tatuadores

O sistema deve permitir que o administrador cadastre tatuadores, informando nome, especialidade, registro profissional e a sala exclusiva que lhe é atribuída como padrão.

**Atores:** Administrador.

**Prioridade:** Essencial

- Critério de aceitação: O tatuador cadastrado passa a aparecer na lista de profissionais disponíveis para orçamento e agendamento.

### [RF-04] Cadastro de Salas

O sistema deve permitir o cadastro das salas de trabalho do estúdio, classificando cada uma como exclusiva de um tatuador ou compartilhada entre os profissionais.

**Atores:** Administrador.

**Prioridade:** Essencial

- Critério de aceitação: A sala cadastrada torna-se um recurso agendável independente, com seu tipo de uso registrado.

### [RF-05] Solicitação de Orçamento

O sistema deve permitir que o cliente solicite um orçamento informando descrição da ideia, região do corpo, tamanho aproximado e imagem de referência do desenho.

**Atores:** Cliente.

**Prioridade:** Essencial

- Critério de aceitação: A solicitação é registrada com status "Orçamento solicitado" e fica visível na fila de análise do tatuador escolhido.

### [RF-06] Análise e Emissão de Orçamento

O sistema deve permitir que o tatuador analise a solicitação e informe o valor total, o número estimado de sessões e a duração prevista de cada sessão.

**Atores:** Tatuador.

**Prioridade:** Essencial

- Critério de aceitação: O orçamento emitido é enviado ao cliente e o projeto passa ao status "Desenho em aprovação".

### [RF-07] Aprovação de Orçamento e Desenho

O sistema deve permitir que o cliente aprove ou recuse o orçamento e o desenho apresentados, registrando data e hora da decisão.

**Atores:** Cliente.

**Prioridade:** Essencial

- Critério de aceitação: Com a aprovação de ambos, o projeto passa ao status "Aprovado" e o botão de agendamento é habilitado.

### [RF-08] Agendamento de Sessão

O sistema deve permitir o agendamento de sessões verificando simultaneamente a disponibilidade do tatuador e a da sala escolhida, que pode ser a sala exclusiva do profissional ou uma sala compartilhada.

**Atores:** Cliente, Tatuador e Recepção.

**Prioridade:** Essencial

- Critério de aceitação: O horário só é confirmado se os dois recursos estiverem livres; caso contrário, o sistema exibe os próximos horários disponíveis por sala.

### [RF-09] Registro de Execução da Sessão

O sistema deve permitir que o tatuador registre a sessão como realizada, informando área trabalhada, duração real, observações e fotos de acompanhamento.

**Atores:** Tatuador.

**Prioridade:** Essencial

- Critério de aceitação: A sessão passa ao status "Realizada" e o projeto entra automaticamente em cicatrização.

### [RF-10] Registro de Materiais Utilizados

O sistema deve permitir o registro dos materiais esterilizados consumidos na sessão, identificados por lote, no momento do encerramento da sessão.

**Atores:** Tatuador.

**Prioridade:** Essencial

- Critério de aceitação: Os materiais informados são vinculados permanentemente à sessão, permitindo rastreabilidade posterior por cliente e por lote.

### [RF-11] Baixa Automática de Estoque

O sistema deve reduzir automaticamente a quantidade disponível dos lotes informados ao concluir o registro da sessão.

**Atores:** Tatuador.

**Prioridade:** Essencial

- Critério de aceitação: A quantidade em estoque do lote é decrementada e o movimento é registrado no histórico de movimentação.

### [RF-12] Cadastro de Materiais e Lotes

O sistema deve permitir o cadastro de materiais e de seus respectivos lotes, com quantidade, data de validade e nível mínimo de estoque.

**Atores:** Administrador.

**Prioridade:** Essencial

- Critério de aceitação: O lote cadastrado passa a estar disponível para seleção no registro de sessões.

### [RF-13] Alerta de Estoque Mínimo

O sistema deve exibir alerta no painel do administrador quando a quantidade de um material atingir ou ficar abaixo do nível mínimo configurado.

**Atores:** Administrador.

**Prioridade:** Importante

- Critério de aceitação: Ao atingir o limite, o material aparece destacado na lista de estoque crítico do painel.

### [RF-14] Controle do Prazo de Cicatrização

O sistema deve calcular automaticamente a data de liberação da próxima sessão a partir da data da sessão realizada e do prazo de cicatrização configurado.

**Atores:** Tatuador e Recepção.

**Prioridade:** Essencial

- Critério de aceitação: A data de liberação é exibida no projeto e as tentativas de agendamento anteriores a ela são recusadas.

### [RF-15] Liberação Automática da Agenda

O sistema deve liberar automaticamente o agendamento da próxima sessão assim que a data de liberação for atingida, sem intervenção manual.

**Atores:** Tatuador e Recepção.

**Prioridade:** Essencial

- Critério de aceitação: Decorrido o prazo, o projeto muda de "Em cicatrização" para "Apto a agendar" na primeira consulta realizada após a data.

### [RF-16] Conclusão do Projeto

O sistema deve permitir que o tatuador marque o projeto como concluído após a última sessão prevista, encerrando o ciclo de execução.

**Atores:** Tatuador.

**Prioridade:** Essencial

- Critério de aceitação: O projeto passa ao status "Concluído" e o período de garantia é iniciado automaticamente.

### [RF-17] Abertura Automática de Garantia

O sistema deve iniciar automaticamente o período de garantia de retoque na data de conclusão do projeto, conforme prazo configurado pelo estúdio.

**Atores:** Administrador.

**Prioridade:** Essencial

- Critério de aceitação: O projeto exibe a data limite da garantia e o botão de solicitação de retoque fica habilitado ao cliente.

### [RF-18] Solicitação de Retoque

O sistema deve permitir que o cliente solicite um retoque; se a solicitação ocorrer dentro do prazo de garantia, o fluxo segue sem geração de novo orçamento.

**Atores:** Cliente.

**Prioridade:** Essencial

- Critério de aceitação: A solicitação dentro do prazo gera uma sessão de retoque sem valor associado; fora do prazo, o sistema abre um novo projeto a partir da etapa de orçamento.

### [RF-19] Publicação no Portfólio

O sistema deve permitir a publicação de projetos concluídos no portfólio público do tatuador, condicionada à autorização expressa do cliente.

**Atores:** Tatuador e Cliente.

**Prioridade:** Importante

- Critério de aceitação: Somente projetos com autorização registrada são exibidos na galeria pública; os demais permanecem restritos.

### [RF-20] Acompanhamento pelo Cliente

O sistema deve permitir que o cliente consulte o andamento do seu projeto, o histórico de sessões realizadas e a data prevista para a próxima sessão.

**Atores:** Cliente.

**Prioridade:** Importante

- Critério de aceitação: O cliente visualiza a linha do tempo do projeto com o estado atual destacado.

### [RF-21] Painel Gerencial

O sistema deve apresentar ao administrador indicadores de projetos em andamento, sessões realizadas no período, taxa de conclusão e materiais em nível crítico.

**Atores:** Administrador.

**Prioridade:** Importante

- Critério de aceitação: Os indicadores são atualizados a cada carregamento do painel e refletem os dados vigentes na base.

### [RF-22] Cancelamento de Sessão

O sistema deve permitir o cancelamento de uma sessão agendada, liberando o tatuador e a sala para novos agendamentos naquele horário.

**Atores:** Cliente, Tatuador e Recepção.

**Prioridade:** Importante

- Critério de aceitação: Após o cancelamento, o horário volta a aparecer como disponível na agenda de ambos os recursos.

### [RF-23] Exportação do Histórico de Materiais

O sistema deve permitir a exportação em PDF do histórico de materiais utilizados por período, para subsidiar a comprovação sanitária.

**Atores:** Administrador.

**Prioridade:** Desejável

- Critério de aceitação: O arquivo gerado lista sessão, data, cliente, material e lote utilizados no período selecionado.
## Requisitos não funcionais

### [NF-01] Desempenho

O tempo de carregamento das telas de listagem não deve exceder 3 segundos em rede de banda larga.

**Prioridade:** Importante

- Critério de aceitação: Teste com ferramenta de medição (Lighthouse ou GTmetrix), registrando a média de cinco execuções.

### [NF-02] Segurança de Credenciais

As senhas dos usuários devem ser armazenadas com algoritmo de hash com sal (BCrypt), nunca em texto puro.

**Prioridade:** Essencial

- Critério de aceitação: Inspeção direta na tabela de usuários demonstrando a ausência de senhas legíveis.

### [NF-03] Controle de Autorização

Toda requisição à API deve validar o token de autenticação e o perfil do usuário antes de executar a operação.

**Prioridade:** Essencial

- Critério de aceitação: Requisições sem token ou com perfil indevido devem retornar os códigos HTTP 401 e 403, respectivamente.

### [NF-04] Usabilidade e Responsividade

A interface deve adaptar-se a telas de 360 px a 1920 px de largura sem perda de funcionalidade ou quebra de layout.

**Prioridade:** Essencial

- Critério de aceitação: Verificação nas resoluções de 360, 768, 1366 e 1920 px utilizando as ferramentas de desenvolvedor do navegador.

### [NF-05] Disponibilidade

O sistema deve permanecer disponível durante o horário comercial de funcionamento do estúdio, com indisponibilidade planejada comunicada previamente.

**Prioridade:** Importante

- Critério de aceitação: Monitoramento do tempo de atividade durante o período de testes, com meta de 98%.

### [NF-06] Compatibilidade

O sistema deve funcionar corretamente nas versões atuais dos navegadores Google Chrome, Mozilla Firefox e Microsoft Edge.

**Prioridade:** Importante

- Critério de aceitação: Execução do roteiro de testes funcionais nos três navegadores, sem divergência de comportamento.

### [NF-07] Proteção de Dados Pessoais

O sistema deve tratar dados pessoais e imagens corporais dos clientes em conformidade com a Lei nº 13.709/2018 (LGPD), exigindo consentimento explícito para publicação de imagens.

**Prioridade:** Essencial

- Critério de aceitação: Nenhuma imagem é exibida publicamente sem registro do consentimento correspondente na base de dados.

### [NF-08] Manutenibilidade

O código deve ser organizado em camadas com responsabilidades separadas, permitindo alterar a regra de negócio sem modificar a camada de apresentação.

**Prioridade:** Essencial

- Critério de aceitação: Revisão da estrutura de pacotes demonstrando a separação entre controlador, serviço, domínio e repositório.

### [NF-09] Rastreabilidade e Auditoria

O sistema deve registrar data, hora e usuário responsável por cada transição de estado do projeto de tatuagem.

**Prioridade:** Essencial

- Critério de aceitação: Consulta ao histórico do projeto exibindo a sequência completa de transições com autoria e horário.

### [NF-10] Integridade de Dados

O banco de dados deve garantir integridade referencial por meio de chaves estrangeiras e restrições de unicidade.

**Prioridade:** Essencial

- Critério de aceitação: Tentativas de exclusão de registros com dependências devem ser recusadas pelo próprio SGBD.

### [NF-11] Cópia de Segurança

A base de dados deve possuir rotina de cópia de segurança diária durante o período de operação.

**Prioridade:** Desejável

- Critério de aceitação: Comprovação da existência de arquivos de backup datados no diretório configurado.

### [NF-12] Portabilidade

A aplicação deve poder ser executada em ambiente containerizado, independentemente do sistema operacional hospedeiro.

**Prioridade:** Desejável

- Critério de aceitação: Execução bem-sucedida da aplicação a partir do arquivo de composição de contêineres em máquina limpa.
## Regras de negócio

### [RN-01] Dupla Aprovação para Agendamento

Nenhuma sessão pode ser agendada enquanto o orçamento e o desenho não estiverem ambos aprovados pelo cliente.

**Prioridade:** Essencial

- Critério de aceitação: Tentativas de agendamento em projeto sem as duas aprovações são recusadas com mensagem explicativa.

### [RN-02] Disponibilidade Cruzada de Recursos

Uma sessão só pode ser confirmada se o tatuador e a sala estiverem simultaneamente livres no horário solicitado.

**Prioridade:** Essencial

- Critério de aceitação: O sistema recusa o agendamento quando qualquer um dos dois recursos estiver ocupado no intervalo informado.

### [RN-03] Uso das Salas Exclusivas e Compartilhadas

Cada tatuador possui uma sala exclusiva, utilizada como padrão em seus agendamentos. As salas compartilhadas, destinadas a peças de grande porte, podem ser utilizadas por qualquer tatuador, desde que não estejam ocupadas no horário pretendido.

**Prioridade:** Essencial

- Critério de aceitação: A sala exclusiva é sugerida automaticamente; ao selecionar uma sala compartilhada, o sistema verifica se ela já está reservada por outro profissional e recusa o agendamento em caso de conflito.

### [RN-04] Prazo de Cicatrização

O intervalo mínimo entre duas sessões do mesmo projeto é de 21 dias corridos, valor parametrizável pelo administrador do estúdio.

**Prioridade:** Essencial

- Critério de aceitação: Agendamentos anteriores à data de liberação calculada são bloqueados pelo sistema.

### [RN-05] Responsabilidade pelo Registro da Sessão

Somente o tatuador responsável pelo projeto pode registrar a sessão como realizada.

**Prioridade:** Essencial

- Critério de aceitação: Outros usuários, inclusive o administrador, não visualizam a ação de encerramento da sessão.

### [RN-06] Obrigatoriedade do Registro de Materiais

Nenhuma sessão pode ser encerrada sem que ao menos um material esterilizado tenha sido informado, garantindo rastreabilidade sanitária.

**Prioridade:** Essencial

- Critério de aceitação: O encerramento é bloqueado enquanto a lista de materiais estiver vazia.

### [RN-07] Impedimento por Estoque Insuficiente

Não é permitido registrar o consumo de um lote cuja quantidade disponível seja inferior à informada.

**Prioridade:** Essencial

- Critério de aceitação: O sistema recusa a operação e informa a quantidade efetivamente disponível no lote.

### [RN-08] Vedação de Lote Vencido

Lotes com data de validade expirada não podem ser selecionados no registro de sessões.

**Prioridade:** Essencial

- Critério de aceitação: Lotes vencidos não aparecem na lista de seleção e são sinalizados no painel de estoque.

### [RN-09] Prazo de Garantia de Retoque

O período de garantia de retoque é de 90 dias corridos a partir da conclusão do projeto, valor parametrizável pelo administrador.

**Prioridade:** Essencial

- Critério de aceitação: A data limite da garantia é calculada e exibida no momento da conclusão do projeto.

### [RN-10] Gratuidade do Retoque na Garantia

Retoques solicitados dentro do prazo de garantia não geram novo orçamento nem cobrança adicional.

**Prioridade:** Essencial

- Critério de aceitação: A sessão de retoque criada dentro do prazo é registrada com valor igual a zero.

### [RN-11] Reabertura de Ciclo Fora da Garantia

Solicitações de retoque posteriores ao prazo de garantia iniciam um novo projeto a partir da etapa de orçamento.

**Prioridade:** Essencial

- Critério de aceitação: O sistema cria um novo projeto vinculado ao anterior por referência histórica.

### [RN-12] Consentimento para Portfólio

Imagens de projetos só podem ser exibidas no portfólio público mediante autorização expressa e registrada do cliente, em conformidade com a LGPD.

**Prioridade:** Essencial

- Critério de aceitação: Projetos sem autorização registrada nunca são retornados pela consulta pública do portfólio.

### [RN-13] Restrição de Idade

Não é permitido abrir projeto de tatuagem para cliente menor de 18 anos sem registro de autorização do responsável legal.

**Prioridade:** Essencial

- Critério de aceitação: O sistema exige o campo de autorização do responsável quando a data de nascimento indicar menoridade.

### [RN-14] Antecedência para Cancelamento

O cancelamento de sessão pelo cliente deve ocorrer com no mínimo 24 horas de antecedência em relação ao horário agendado.

**Prioridade:** Importante

- Critério de aceitação: Solicitações com menos de 24 horas exigem intervenção da recepção para serem efetivadas.

### [RN-15] Exclusividade dos Cadastros Estruturais

Apenas usuários com perfil "Administrador" podem cadastrar, editar ou inativar tatuadores, salas, materiais e parâmetros de negócio.

**Prioridade:** Essencial

- Critério de aceitação: Usuários de outros perfis não visualizam o menu de cadastros estruturais.

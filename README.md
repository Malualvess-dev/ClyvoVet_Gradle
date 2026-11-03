# 🐾 ClyvoVet

Aplicação web em **Spring Boot 4 + Thymeleaf** que conecta **tutores** e **veterinários**: o tutor cadastra seus pets e solicita consultas; o veterinário confirma ou recusa, registra o atendimento (prontuário e vacinas) e o tutor acompanha o histórico clínico e os reforços de vacina — com notificações automáticas em cada etapa.

## Integrantes

| Nome | RM |
|---|---|
| Maria Luiza Alves de Aquino | RM561802 |
| Natan Freitas De Moraes | RM564992 |
| João Victor Gomes de Souza | RM560907 |
| Lucas Barranha Giannini | RM564508 |

## Sumário

- [Tecnologias](#tecnologias)
- [Como executar](#como-executar)
- [Contas de demonstração](#contas-de-demonstração)
- [Login com Google e GitHub (OAuth2)](#login-com-google-e-github-oauth2)
- [Banco Oracle (opcional)](#banco-oracle-opcional)
- [Requisitos da entrega](#requisitos-da-entrega)
- [Fluxos de negócio](#fluxos-de-negócio)
- [Arquitetura](#arquitetura)
- [Testes](#testes)

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.0 (Web MVC, Data JPA, Validation) |
| Frontend | Thymeleaf (renderizado no servidor) + CSS próprio |
| Segurança | Spring Security 7 — login por sessão (formulário) + OAuth2 (Google/GitHub) |
| Banco | H2 em memória (padrão, modo Oracle) ou Oracle |
| Versionamento do banco | Flyway |
| Testes | JUnit 5, AssertJ, Spring Security Test, MockMvc |

## Como executar

Pré-requisito: **JDK 21**. Não é preciso instalar Gradle nem banco de dados.

```bash
git clone https://github.com/Malualvess-dev/ClyvoVet_Gradle.git
cd ClyvoVet_Gradle
./gradlew bootRun          # Windows: gradlew.bat bootRun
```

Acesse **http://localhost:8080** — você será levado à tela de login.

Por padrão a aplicação usa o perfil `dev` (H2 em memória). Ao iniciar, o Flyway cria as tabelas e insere os dados de demonstração. Como o banco é em memória, os dados voltam ao estado inicial a cada reinício.

## Contas de demonstração

São os usuários do script do grupo de Banco de Dados (migration `V2__inserir_dados_do_grupo.sql`). **Senha de todas: `Clyvo@2026`**

| Perfil | E-mail | O que já existe |
|---|---|---|
| Tutor | `joao.tutor@clyvo.com` | Pet Thor, com consulta realizada, prontuário, duas vacinas e leituras da coleira |
| Tutor | `lucas.tutor@clyvo.com` | Pet Mingau |
| Veterinário | `vet.roberto@clyvo.com` | Dr. Roberto Santos, clínica geral |
| Veterinário | `vet.camila@clyvo.com` | Dra. Camila Lima, dermatologia |

Os demais usuários do script (`malu.tutor`, `ana.tutor`, `carlos.tutor`, `vet.fernando`, `vet.beatriz`, `vet.gabriel`, todos `@clyvo.com`) também entram com a mesma senha.

Também é possível criar uma conta nova em **/cadastro** e escolher o perfil em seguida.

## Login com Google e GitHub (OAuth2)

O login social é ativado **apenas se as credenciais estiverem configuradas** por variáveis de ambiente. Sem elas, a aplicação funciona normalmente e os botões não aparecem. Nenhum segredo fica no repositório.

| Variável | Onde obter |
|---|---|
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | Google Cloud Console → APIs e serviços → Credenciais → ID do cliente OAuth |
| `GITHUB_CLIENT_ID` / `GITHUB_CLIENT_SECRET` | GitHub → Settings → Developer settings → OAuth Apps |

URLs de callback para cadastrar nos provedores:

- Google: `http://localhost:8080/login/oauth2/code/google`
- GitHub: `http://localhost:8080/login/oauth2/code/github`

Exemplo (PowerShell):

```powershell
$env:GITHUB_CLIENT_ID="seu-client-id"
$env:GITHUB_CLIENT_SECRET="seu-client-secret"
./gradlew bootRun
```

No primeiro acesso via Google/GitHub a conta é criada automaticamente e o usuário escolhe o perfil (tutor ou veterinário) antes de acessar o sistema.

## Banco Oracle

A aplicação usa o **mesmo banco relacional da disciplina de Banco de Dados**: as 20 tabelas, as sequences, os dados iniciais, os packages e o trigger do grupo. Para usar Oracle, ative o perfil `oracle` e informe a conexão por variáveis de ambiente:

```powershell
$env:SPRING_PROFILES_ACTIVE="oracle"
$env:DB_URL="jdbc:oracle:thin:@host:1521:ORCL"
$env:DB_USERNAME="seu_usuario"
$env:DB_PASSWORD="sua_senha"
./gradlew bootRun
```

As migrations comuns (`db/migration`) usam SQL compatível com Oracle e com o H2 em modo Oracle. Os packages e o trigger (`db/oracle`) só rodam no Oracle.

**Primeira execução num usuário Oracle que já tinha as tabelas:** rode uma vez o script [`infra/oracle/limpar-schema-clyvovet.sql`](infra/oracle/limpar-schema-clyvovet.sql) no SQL Developer (F5). Ele remove as tabelas antigas e o histórico do Flyway; ao subir, a aplicação recria tudo com os dados do grupo.

### Integração com as procedures do grupo

| Objeto do banco | Onde a aplicação usa |
|---|---|
| `PKG_PETS_JSON_API.PR_EXPORTAR_PETS_JSON` | Área do veterinário → **Banco de dados**: JSON de todos os pets |
| `PKG_RELATORIOS_API.PR_RELATORIO_COLEIRA` | Área do veterinário → **Banco de dados**: relatório de batimentos por pet |
| `PKG_RELATORIOS.FN_CALCULAR_IDADE_PET` | Prontuário do pet (idade exibida ao tutor) |
| `TRG_AUDITORIA_COLEIRA` + `PKG_AUDITORIA_COLEIRA` | Ao gravar uma leitura da coleira pela tela **Banco de dados**, o trigger registra a operação em `TB_AUDITORIA_COLEIRA` |
| `TB_LOG_ERRO` | Erros das procedures chamadas pela aplicação ficam registrados nesta tabela |

### Como a aplicação usa cada tabela

| Tabela | Uso na aplicação |
|---|---|
| `TB_USUARIO`, `TB_TUTOR`, `TB_VETERINARIO` | Login, perfis e cadastro |
| `TB_PET`, `TB_RACA`, `TB_ESPECIE` | Cadastro de pets (raça escolhida por espécie) |
| `TB_CONSULTA` | Fluxo de agendamento |
| `TB_PRONTUARIO`, `TB_PRESCRICAO` | Fluxo de atendimento |
| `TB_HISTORICO_SAUDE` | Vacinas, exames e reforços previstos |
| `TB_NOTIFICACAO` | Avisos de cada etapa |
| `TB_COLEIRA`, `TB_AUDITORIA_COLEIRA` | Leituras da coleira inteligente e auditoria |
| `TB_AGENDA` | Lembretes pessoais (cadastro, alteração e exclusão) |
| `TB_ANEXO` | Documentos do usuário |
| `TB_ENDERECO`, `TB_BAIRRO`, `TB_CIDADE`, `TB_ESTADO` | Endereço do usuário |
| `TB_LOG_ERRO` | Erros das procedures |

## Requisitos da entrega

### 1. Frontend (Thymeleaf)

- Páginas renderizadas no servidor, com layout e componentes reutilizáveis (`templates/fragments`).
- Menu e telas diferentes para cada perfil; mensagens de sucesso e erro após cada ação.
- Formulários exibem os erros de validação ao lado de cada campo, sem perder o que foi digitado.
- Páginas próprias para acesso negado (403) e recurso não encontrado (404).

### 2. Flyway

| Versão | Migration |
|---|---|
| V1 | `criar_estrutura_do_banco` — as 20 tabelas, chaves e sequences do modelo do grupo |
| V2 | `inserir_dados_do_grupo` — dados iniciais do script do grupo |
| V3 | `ajustar_tabelas_para_a_aplicacao` — evolução de schema: nome e provedor do usuário, peso do pet, status SOLICITADA/RECUSADA, justificativa, reforço de vacina |
| V4 | `complementar_dados_de_demonstracao` — raças comuns e reforços de vacina |
| R__ (só Oracle) | packages `PKG_PETS_JSON(_API)`, `PKG_RELATORIOS(_API)`, `PKG_AUDITORIA_COLEIRA` e trigger `TRG_AUDITORIA_COLEIRA` |

O Hibernate **não** altera o schema (`ddl-auto=none`): o Flyway é o único responsável pela estrutura do banco.

### 3. Spring Security

- **Dois perfis** (`TUTOR` e `VETERINARIO`), mais o estado `PENDENTE` para contas que ainda não escolheram o perfil.
- **Rotas protegidas no servidor por perfil** (`SecurityConfig`):

| Rota | Acesso |
|---|---|
| `/login`, `/cadastro`, `/css/**` | Público |
| `/cadastro/perfil/**` | Apenas contas com perfil pendente |
| `/tutor/**` | Apenas `TUTOR` |
| `/veterinario/**` | Apenas `VETERINARIO` |
| Demais rotas | Usuário autenticado |

- Sessão no servidor, sem token para copiar ou colar; senhas com BCrypt; proteção CSRF em todos os formulários.
- Login por e-mail/senha **e** OAuth2 (Google e GitHub).
- Além da rota, cada consulta, pet e notificação é buscada junto com o usuário logado: um usuário não acessa dados de outro mesmo que altere o ID na URL.

### 4. Funcionalidades completas

Dois fluxos de negócio com regras além do CRUD, descritos abaixo, e validações em todos os formulários (Bean Validation: CPF, CRMV, telefone, e-mail, força de senha, datas, faixas de peso etc.).

## Fluxos de negócio

### Fluxo 1 — Agendamento de consulta

```
Tutor solicita ──► SOLICITADA ──► Veterinário confirma ──► CONFIRMADA
                       │                                       │
                       ├─► Veterinário recusa ──► RECUSADA     └─► Tutor cancela (≥ 24h) ──► CANCELADA
                       └─► Tutor cancela ──► CANCELADA
```

No banco, a consulta confirmada fica com status `AGENDADA` e a concluída com `REALIZADA`, os mesmos valores do modelo do grupo (`StatusConsultaConverter`). O tutor escolhe a modalidade: presencial ou online.

Regras (`PoliticaDeAgendamento`, `AgendamentoService` e `Consulta`):

- Atendimento de segunda a sábado, das 08:00 às 17:30, em intervalos de 30 minutos.
- Antecedência mínima de 1 hora para solicitar.
- O tutor só vê e só consegue reservar **horários livres** do veterinário escolhido.
- Um pet não pode ter duas consultas ativas no mesmo dia.
- Recusa e cancelamento exigem justificativa (5 a 250 caracteres).
- Consulta confirmada só pode ser cancelada com no mínimo **24 horas** de antecedência.
- Cada transição gera uma **notificação** automática para a outra parte.

### Fluxo 2 — Atendimento e prontuário

```
CONFIRMADA ──► Veterinário registra o atendimento ──► CONCLUIDA
                  ├─ TB_PRONTUARIO: sintomas, diagnóstico, tratamento, peso, temperatura, exames e retorno
                  ├─ TB_PRESCRICAO: medicamento, dieta ou restrição (opcional)
                  ├─ atualiza o peso do pet
                  ├─ TB_HISTORICO_SAUDE: vacina aplicada e próxima dose (opcional)
                  └─ notifica o tutor
```

Regras (`AtendimentoService`, `Consulta` e `Vacina`):

- Só consultas **confirmadas** podem ser atendidas, e somente a partir do dia marcado.
- Só o veterinário responsável pela consulta pode registrá-la.
- A próxima dose precisa ser posterior à aplicação e exige o nome da vacina.
- O tutor passa a ver o **histórico clínico** e a **carteira de vacinação** do pet; o painel mostra os reforços previstos para os próximos 30 dias.

## Arquitetura

```
src/main/java/br/com/fiap/clyvovet
├── config          # Relógio da aplicação (fuso America/Sao_Paulo)
├── controller      # Controllers MVC
│   ├── tutor/        # PetController, TutorConsultaController, TutorPainelController
│   ├── veterinario/  # VeterinarioAgendaController, AtendimentoController, BancoDeDadosController
│   ├── conta/        # Lembretes (TB_AGENDA), documentos (TB_ANEXO) e endereço (TB_ENDERECO)
│   └── advice/       # Dados globais das telas e tratamento de erros
├── dto
│   ├── request     # Dados recebidos dos formulários, com Bean Validation (*Request)
│   └── response    # Dados enviados às telas (*Response, records imutáveis)
├── exception       # RegraNegocioException, RecursoNaoEncontradoException
├── mapper          # Conversão entidade → Response
├── model           # Entidades JPA das tabelas do grupo, com as regras de negócio
│   └── conversor/    # Status da consulta e coluna S/N
├── repository      # Spring Data JPA
├── security        # SecurityConfig, OAuth2, usuário logado e sessão
└── service         # Casos de uso (agendamento, atendimento, contas...)
    └── banco/        # Chamada das procedures do Oracle (com versão vazia para o H2)

src/main/resources/db
├── migration       # Flyway: estrutura e dados (H2 e Oracle)
└── oracle          # Flyway: packages e trigger do grupo (só Oracle)
```

Fluxo de uma requisição: **Controller** recebe o `*Request` validado → **Service** aplica as regras sobre as entidades (`model`) → **Mapper** converte para `*Response` → **Controller** entrega o response ao template Thymeleaf. As telas nunca recebem entidades JPA.

Decisões de projeto:

- **As regras de estado ficam na entidade.** `Consulta` expõe `confirmar()`, `recusar()`, `cancelar()` e `concluir()` e não permite transições inválidas; os services só orquestram.
- **Responsabilidades separadas:** `PoliticaDeAgendamento` cuida dos horários, `AgendamentoService` do fluxo 1, `AtendimentoService` do fluxo 2 e `ConsultaService` das consultas de leitura.
- **Data e hora testáveis:** os serviços recebem um `Clock` injetado, o que permite testar regras de horário de forma determinística.
- **Erros de negócio tratados em um único lugar:** `TratamentoDeErrosAdvice` mostra a mensagem na própria página de origem.

## Testes

```bash
./gradlew test
```

75 testes:

- `ConsultaTest`, `RegrasDoModeloTest` e `PoliticaDeAgendamentoTest` — regras de domínio (transições, 24h, horários, domingo, retorno, vacinas, alerta da coleira, conversão dos status para o modelo do grupo).
- `FluxosDeNegocioTest` — os dois fluxos completos sobre o banco do grupo criado pelo Flyway, conferindo os valores gravados nas tabelas.
- `MinhaContaTest` — cadastro, alteração e exclusão de lembretes, documentos, endereço e leituras da coleira.
- `PaginasESegurancaTest` — todas as páginas carregam, cada perfil é bloqueado na área do outro (403), dados de outro usuário não são encontrados (404), CSRF e validações dos formulários.

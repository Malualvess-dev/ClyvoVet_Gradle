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

Criadas pela migration `V4__inserir_dados_de_demonstracao.sql`. **Senha de todas: `Clyvo@2026`**

| Perfil | E-mail | O que já existe |
|---|---|---|
| Tutor | `carla@clyvovet.com` | Pets Thor e Mia; Thor com uma consulta concluída e duas vacinas |
| Veterinário | `ana.vet@clyvovet.com` | Clínica geral |
| Veterinário | `bruno.vet@clyvovet.com` | Dermatologia |

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

## Banco Oracle (opcional)

Para usar Oracle, ative o perfil `oracle` e informe a conexão por variáveis de ambiente:

```powershell
$env:SPRING_PROFILES_ACTIVE="oracle"
$env:DB_URL="jdbc:oracle:thin:@host:1521:ORCL"
$env:DB_USERNAME="seu_usuario"
$env:DB_PASSWORD="sua_senha"
./gradlew bootRun
```

As migrations usam SQL compatível com Oracle e com o H2 em modo Oracle, e as tabelas usam o prefixo `TB_CV_` para não conflitar com tabelas antigas.

## Requisitos da entrega

### 1. Frontend (Thymeleaf)

- Páginas renderizadas no servidor, com layout e componentes reutilizáveis (`templates/fragments`).
- Menu e telas diferentes para cada perfil; mensagens de sucesso e erro após cada ação.
- Formulários exibem os erros de validação ao lado de cada campo, sem perder o que foi digitado.
- Páginas próprias para acesso negado (403) e recurso não encontrado (404).

### 2. Flyway

| Versão | Migration |
|---|---|
| V1 | `criar_tabelas_de_usuarios` — usuários, tutores, veterinários |
| V2 | `criar_tabelas_de_pets_e_consultas` — pets, consultas, notificações |
| V3 | `adicionar_prontuario_e_vacinas` — evolução de schema: novas colunas em consulta + tabela de vacinas |
| V4 | `inserir_dados_de_demonstracao` — contas e dados de demonstração |

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
                  ├─ diagnóstico, prescrição e peso aferido
                  ├─ atualiza o peso do pet
                  ├─ registra a vacina aplicada e a próxima dose (opcional)
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
│   ├── veterinario/  # VeterinarioAgendaController, AtendimentoController
│   └── advice/       # Dados globais das telas e tratamento de erros
├── dto
│   ├── request     # Dados recebidos dos formulários, com Bean Validation (*Request)
│   └── response    # Dados enviados às telas (*Response, records imutáveis)
├── exception       # RegraNegocioException, RecursoNaoEncontradoException
├── mapper          # Conversão entidade → Response
├── model           # Entidades JPA com as regras de transição de status
├── repository      # Spring Data JPA
├── security        # SecurityConfig, OAuth2, usuário logado e sessão
└── service         # Casos de uso (agendamento, atendimento, contas...)
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

41 testes:

- `ConsultaTest` e `PoliticaDeAgendamentoTest` — regras de domínio (transições, 24h, horários, domingo).
- `FluxosDeNegocioTest` — os dois fluxos completos sobre o banco criado pelo Flyway.
- `PaginasESegurancaTest` — todas as páginas carregam, cada perfil é bloqueado na área do outro (403), visitantes são redirecionados ao login, CSRF e validações dos formulários.

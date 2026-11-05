# 🐾 ClyvoVet

Aplicação web em **Spring Boot 4 + Thymeleaf** que conecta **tutores** e **veterinários**: o tutor cadastra seus pets e solicita consultas; o veterinário confirma ou recusa, registra o atendimento (prontuário e vacinas) e o tutor acompanha o histórico clínico e os reforços de vacina — com notificações automáticas em cada etapa.

O mesmo backend também atende o **app mobile SuperVet** por uma API REST: site e app usam as mesmas contas e o mesmo banco Oracle.

| | |
|---|---|
| 🌐 **Aplicação online** | **https://clyvovet-rm561802.azurewebsites.net** |
| 🎬 **Vídeo de apresentação** | **https://youtu.be/61hxvMsL6PY** |
| 📱 **App mobile (SuperVet)** | https://github.com/FIAP-MOBILE-2026-Fevereiro/2tdspw-challenge-proj-app-pet |
| ☁️ **Hospedagem** | Azure App Service (Linux, Java 21) |
| 🗄️ **Banco de dados** | Oracle da FIAP — modelo do grupo (20 tabelas), versionado com Flyway |

## Integrantes

| Nome | RM |
|---|---|
| Maria Luiza Alves de Aquino | RM561802 |
| Natan Freitas De Moraes | RM564992 |
| João Victor Gomes de Souza | RM560907 |
| Lucas Barranha Giannini | RM564508 |

## Sumário

- [O problema e a solução](#o-problema-e-a-solução)
- [Tecnologias](#tecnologias)
- [Acessar a aplicação online](#acessar-a-aplicação-online)
- [Como executar localmente](#como-executar-localmente)
- [Contas de demonstração](#contas-de-demonstração)
- [Login com Google e GitHub (OAuth2)](#login-com-google-e-github-oauth2)
- [Banco Oracle](#banco-oracle)
- [Deploy na Azure](#deploy-na-azure)
- [Requisitos da entrega](#requisitos-da-entrega)
- [Fluxos de negócio](#fluxos-de-negócio)
- [API REST do aplicativo mobile](#api-rest-do-aplicativo-mobile)
- [Evidências](#evidências)
- [Arquitetura](#arquitetura)
- [Integração com as demais disciplinas](#integração-com-as-demais-disciplinas)
- [Testes](#testes)

## O problema e a solução

O desafio proposto pela **Clyvo Vet** é a jornada contínua de cuidado do pet. Hoje, o histórico de um animal fica espalhado entre clínicas, papéis e a memória do tutor: consultas são marcadas por telefone, vacinas são esquecidas e o veterinário atende sem saber o que aconteceu antes.

O ClyvoVet reúne essa jornada em um só lugar:

- **Para o tutor:** cadastra os pets, vê só os horários realmente livres do veterinário, acompanha o status da consulta, o histórico clínico, os reforços de vacina previstos e as leituras da coleira inteligente.
- **Para o veterinário:** recebe as solicitações, confirma ou recusa com justificativa e registra o atendimento (prontuário, prescrição e vacinas) já vendo o histórico do pet.
- **Para os dois:** uma notificação automática a cada mudança, no site ou no app.

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.0 (Web MVC, Data JPA, Validation) |
| Frontend | Thymeleaf (renderizado no servidor) + CSS próprio |
| Segurança | Spring Security 7 — login por sessão (formulário) + OAuth2 (Google/GitHub) |
| API para o app mobile | REST + JSON em `/api/**`, autenticação HTTP Basic sem sessão |
| Banco | Oracle da FIAP (produção) · H2 em memória, modo Oracle (desenvolvimento e testes) |
| Versionamento do banco | Flyway (migrations versionadas + packages e trigger do grupo) |
| Nuvem | Azure App Service (Linux) criado via Azure CLI |
| Testes | JUnit 5, AssertJ, Spring Security Test, MockMvc |

## Acessar a aplicação online

Acesse **https://clyvovet-rm561802.azurewebsites.net** e entre com uma das [contas de demonstração](#contas-de-demonstração). A versão online usa o **Oracle da FIAP**, então os dados cadastrados permanecem entre os acessos — e são os mesmos que o app mobile mostra.

## Como executar localmente

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

URLs de callback cadastradas nos provedores:

| Provedor | Local | Azure |
|---|---|---|
| Google | `http://localhost:8080/login/oauth2/code/google` | `https://clyvovet-rm561802.azurewebsites.net/login/oauth2/code/google` |
| GitHub | `http://localhost:8080/login/oauth2/code/github` | `https://clyvovet-rm561802.azurewebsites.net/login/oauth2/code/github` |

Exemplo (PowerShell):

```powershell
$env:GITHUB_CLIENT_ID="seu-client-id"
$env:GITHUB_CLIENT_SECRET="seu-client-secret"
./gradlew bootRun
```

No primeiro acesso via Google/GitHub a conta é criada automaticamente e o usuário escolhe o perfil (tutor ou veterinário) antes de acessar o sistema.

> O app do Google está em modo de teste: o login com Google funciona apenas para os e-mails cadastrados como usuários de teste. O login com GitHub e o login por e-mail e senha funcionam para qualquer pessoa.

## Banco Oracle

A aplicação usa o **mesmo banco relacional da disciplina de Banco de Dados**: as 20 tabelas, as sequences, os dados iniciais, os packages e o trigger do grupo. Para usar Oracle, ative o perfil `oracle` e informe a conexão por variáveis de ambiente:

```powershell
$env:SPRING_PROFILES_ACTIVE="oracle"
$env:DB_URL="jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL"
$env:DB_USERNAME="rmXXXXXX"
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

## Deploy na Azure

Os recursos foram criados via **Azure CLI** pelo script [`infra/criar-recursos-azure.ps1`](infra/criar-recursos-azure.ps1):

| Recurso | Nome | Detalhes |
|---|---|---|
| Grupo de recursos | `rg-clyvovet` | |
| Plano do App Service | `plan-clyvovet` | Linux, SKU B1, região East US |
| Web App | `clyvovet-rm561802` | Java 21, HTTPS obrigatório, Always On |

O script também configura as variáveis de ambiente do Web App (perfil `oracle`, conexão com o banco e credenciais do OAuth2), lendo os segredos da sessão do terminal — nada sensível fica no repositório.

Publicação da aplicação:

```powershell
az login
./gradlew clean bootJar
az webapp deploy --resource-group rg-clyvovet --name clyvovet-rm561802 --src-path build/libs/clyvovet-1.0.0.jar --type jar
```

```
Navegador ──► https://clyvovet-rm561802.azurewebsites.net ──┐
                                                            ├──► Azure App Service (Spring Boot) ──► Oracle FIAP
App SuperVet ──► /api/** (JSON, HTTP Basic) ────────────────┘        (clyvovet-rm561802)          (20 tabelas do grupo)
```

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
| V5 | `observacoes_do_pet_para_o_aplicativo` — campo de observações do pet usado pelo app mobile |
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
| `POST /api/cadastro/**` | Público (cadastro pelo app) |
| `/api/**` | `TUTOR` ou `VETERINARIO`, via HTTP Basic, sem sessão |
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

Regras (`AtendimentoService`, `Consulta`, `Prontuario` e `HistoricoSaude`):

- Só consultas **confirmadas** podem ser atendidas, e somente a partir do dia marcado.
- Só o veterinário responsável pela consulta pode registrá-la.
- A próxima dose precisa ser posterior à aplicação e exige o nome da vacina.
- O tutor passa a ver o **histórico clínico** e a **carteira de vacinação** do pet; o painel mostra os reforços previstos para os próximos 30 dias.

## API REST do aplicativo mobile

O app **SuperVet** (React Native/Expo) usa este mesmo backend no lugar do Firebase: as contas, pets e
consultas criados no app aparecem no site e vice-versa, porque os dois gravam nas mesmas tabelas do Oracle.

- Cadeia de segurança própria para `/api/**` (`SecurityConfig.filtroDaApi`): **HTTP Basic** com o e-mail e a senha da conta (BCrypt), **sem sessão** e sem CSRF; respostas 401/403 em JSON e **CORS** liberado só para `/api/**` (app rodando no navegador).
- As regras são as mesmas do site: os controllers da API chamam `AgendamentoService`, `AtendimentoService` e a entidade `Consulta`. Erros voltam como `{"status", "mensagem", "campos"}` (`TratamentoDeErrosDaApi`).
- O tutor só acessa os próprios pets e consultas; ações exclusivas de um perfil devolvem 403.

| Método e rota | Perfil | O que faz |
|---|---|---|
| `GET /api/me` | Todos | Perfil do usuário logado (usado no login do app) |
| `POST /api/cadastro/tutor` · `POST /api/cadastro/veterinario` | Público | Cria a conta (TB_USUARIO) e o perfil (TB_TUTOR / TB_VETERINARIO) |
| `GET /api/pets` | Todos | Tutor: os seus pets · Veterinário: todos os pacientes |
| `POST /api/pets` · `PUT /api/pets/{id}` · `DELETE /api/pets/{id}` | Tutor | Cadastra, edita e exclui (só pets sem histórico) |
| `GET /api/racas` | Todos | Raças e espécies do banco |
| `GET /api/veterinarios` · `GET /api/veterinarios/{id}/horarios?data=aaaa-mm-dd` | Todos | Veterinários e horários livres |
| `GET /api/consultas` | Todos | Tutor: consultas dos pets · Veterinário: a própria agenda |
| `POST /api/consultas` | Tutor | Solicita consulta |
| `POST /api/consultas/{id}/remarcacao` | Tutor | Remarca (volta para "solicitada") |
| `POST /api/consultas/{id}/cancelamento` | Ambos | Tutor cancela · Veterinário recusa a solicitação ou cancela a confirmada |
| `POST /api/consultas/{id}/confirmacao` | Veterinário | Confirma |
| `POST /api/consultas/{id}/atendimento` | Veterinário | Finaliza: a observação vira o diagnóstico do prontuário |
| `DELETE /api/consultas/{id}` | Tutor | Exclui consulta cancelada ou recusada |

Teste rápido (PowerShell, com a aplicação rodando):

```powershell
curl.exe -u joao.tutor@clyvo.com:Clyvo@2026 http://localhost:8080/api/me
curl.exe -u joao.tutor@clyvo.com:Clyvo@2026 http://localhost:8080/api/pets
```

## Evidências

Todas as evidências estão no **[vídeo de apresentação](https://youtu.be/61hxvMsL6PY)**, gravado sobre a aplicação online:

- Recursos na Azure (`rg-clyvovet`) e o Web App rodando com o perfil `oracle`.
- Fluxo 1 no site: o tutor solicita a consulta, o veterinário confirma e os dois recebem notificações.
- Fluxo 2 no site: atendimento com prontuário, prescrição e vacina, visto depois pela tutora.
- Segurança: acesso negado à área do outro perfil e login com Google/GitHub.
- Tela **Banco de dados**: procedures do grupo executadas pela aplicação (JSON dos pets, relatório da coleira e trigger de auditoria).
- App SuperVet chamando a API da Azure (aba Network) e a consulta criada no app aparecendo no site.
- SQL Developer mostrando os registros gravados no Oracle.

Para conferir no Oracle um dado criado pelo site ou pelo app:

```sql
SELECT c.ID_CONSULTA, c.DATA_HORA, c.STATUS, p.NOME AS PET, v.NOME AS VETERINARIO
FROM TB_CONSULTA c
JOIN TB_PET p         ON p.ID_PET = c.ID_PET
JOIN TB_VETERINARIO v ON v.ID_VETERINARIO = c.ID_VETERINARIO
ORDER BY c.ID_CONSULTA DESC;
```

## Arquitetura

```
src/main/java/br/com/fiap/clyvovet
├── config          # Relógio da aplicação (fuso America/Sao_Paulo)
├── controller      # Controllers MVC
│   ├── api/          # API REST do app mobile (/api/**) e erros em JSON
│   ├── tutor/        # PetController, TutorConsultaController, TutorPainelController
│   ├── veterinario/  # VeterinarioAgendaController, AtendimentoController, BancoDeDadosController
│   ├── conta/        # Lembretes (TB_AGENDA), documentos (TB_ANEXO) e endereço (TB_ENDERECO)
│   └── advice/       # Dados globais das telas e tratamento de erros
├── dto
│   ├── api         # Requests e responses da API REST (records)
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

## Integração com as demais disciplinas

| Disciplina | Como foi aplicada no ClyvoVet | Evidência |
|---|---|---|
| **Java Advanced** | Aplicação Spring Boot com Thymeleaf, Flyway, Spring Security (dois perfis + OAuth2), dois fluxos de negócio e API REST para o app | Este repositório |
| **Mastering Relational and Non-Relational Database** | O Java usa o **modelo relacional do grupo** (20 tabelas) e chama os packages, a função e o trigger em PL/SQL | [`db/migration`](src/main/resources/db/migration) · [`db/oracle`](src/main/resources/db/oracle) |
| **DevOps Tools & Cloud Computing** | Recursos criados via **Azure CLI** e aplicação publicada no **Azure App Service** | [`infra/criar-recursos-azure.ps1`](infra/criar-recursos-azure.ps1) |
| **Compliance, Quality Assurance & Tests** | 94 testes automatizados de domínio, fluxos, segurança e API (JUnit 5 + MockMvc) | [`src/test`](src/test) |
| **Mobile Application Development** | App **SuperVet** (React Native/Expo) consumindo a API REST deste backend no lugar do Firebase | [API REST do aplicativo mobile](#api-rest-do-aplicativo-mobile) · [Repositório mobile](https://github.com/FIAP-MOBILE-2026-Fevereiro/2tdspw-challenge-proj-app-pet) |
| **Disruptive Architectures: IoT, IoB & Generative IA** | Leituras da **coleira inteligente** (TB_COLEIRA) com alerta de batimentos | [Tela da coleira no prontuário do pet](#fluxos-de-negócio) |

## Testes

```bash
./gradlew test             # Windows: gradlew.bat test
```

94 testes:

- `ConsultaTest`, `RegrasDoModeloTest` e `PoliticaDeAgendamentoTest` — regras de domínio (transições, 24h, horários, domingo, retorno, vacinas, alerta da coleira, conversão dos status para o modelo do grupo).
- `FluxosDeNegocioTest` — os dois fluxos completos sobre o banco do grupo criado pelo Flyway, conferindo os valores gravados nas tabelas.
- `MinhaContaTest` — cadastro, alteração e exclusão de lembretes, documentos, endereço e leituras da coleira.
- `ApiDoAplicativoTest` — a API do app mobile: login HTTP Basic (401 em JSON), cadastro, CRUD de pets, horários livres e o fluxo completo pelo app (solicitar → confirmar → remarcar → finalizar), além de recusa, cancelamento, exclusão e bloqueios por perfil.
- `PaginasESegurancaTest` — todas as páginas carregam, cada perfil é bloqueado na área do outro (403), dados de outro usuário não são encontrados (404), CSRF e validações dos formulários.

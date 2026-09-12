# 🐾 ClyvoVet API — SuperVet

API REST desenvolvida em **Java com Spring Boot** para gerenciamento de informações relacionadas aos cuidados de pets.

O projeto possui autenticação com **Spring Security + JWT**, controle de acesso por perfil, operações CRUD, documentação Swagger/OpenAPI e uma interface web desenvolvida com **Thymeleaf, HTML, CSS e JavaScript**, apresentada visualmente como **SuperVet**.

---

## 📌 Descrição

O **ClyvoVet** é uma aplicação desenvolvida para centralizar e organizar informações importantes relacionadas aos cuidados de animais de estimação.

A solução permite o gerenciamento de usuários, tutores, veterinários, endereços, agendas, notificações e anexos.

O backend foi desenvolvido como uma **API REST com Spring Boot**, enquanto o frontend utiliza **Thymeleaf, HTML, CSS e JavaScript**.

Na interface, o sistema é apresentado com o nome **SuperVet**.

Além das operações tradicionais de CRUD, o projeto possui:

- Autenticação de usuários;
- Geração e validação de token JWT;
- Controle de acesso baseado no tipo de usuário;
- Criptografia de senhas;
- Validações de dados;
- Paginação;
- Tratamento de exceções;
- DTOs de Request e Response;
- Mappers;
- HATEOAS;
- Swagger/OpenAPI;
- Interface web integrada à API.

---

# 🎯 Objetivo do Projeto

O objetivo do projeto é oferecer uma solução para organização e gerenciamento de informações relacionadas aos cuidados de pets.

A aplicação centraliza informações como:

- Dados dos usuários;
- Dados dos tutores;
- Dados dos veterinários;
- Endereços;
- Compromissos e agendas;
- Notificações;
- Documentos e anexos.

Com isso, informações importantes podem ser acessadas e administradas através de uma única aplicação.

---

# 🛠️ Tecnologias Utilizadas

## Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Spring Security
- JWT (JSON Web Token)
- BCrypt
- Bean Validation
- Spring HATEOAS
- Swagger / OpenAPI
- Gradle
- Oracle Database

## Frontend

- Thymeleaf
- HTML5
- CSS3
- JavaScript
- Fetch API
- LocalStorage

---

# 🔐 Autenticação e Segurança

O projeto utiliza **Spring Security com JWT** para autenticação e autorização dos usuários.

Após realizar o login corretamente, o backend gera um **token JWT**.

Esse token é utilizado pelo frontend nas requisições protegidas através do header:

```http
Authorization: Bearer TOKEN_JWT
```

As senhas dos usuários são armazenadas de forma criptografada utilizando **BCrypt**.

A aplicação utiliza autenticação **stateless**, portanto o servidor não precisa manter uma sessão tradicional para identificar o usuário em cada requisição.

---

# 🔑 Fluxo de Autenticação

O fluxo de autenticação funciona da seguinte maneira:

1. O usuário acessa a página de login.
2. Informa email e senha.
3. O frontend envia os dados para a API.
4. O backend verifica as credenciais.
5. Se as credenciais forem válidas, um token JWT é gerado.
6. O token é apresentado na interface.
7. O usuário pode utilizar o token para acessar o sistema.
8. O token é armazenado no navegador.
9. As requisições protegidas enviam o token no header `Authorization`.
10. O Spring Security valida o token e verifica as permissões do usuário.

---

# 👥 Perfis de Usuário

O sistema possui controle de acesso de acordo com o perfil do usuário.

Atualmente existem dois tipos principais:

### TUTOR

O perfil **TUTOR** possui acesso às funcionalidades relacionadas ao próprio uso do sistema, como:

- Dashboard;
- Agenda;
- Anexos;
- Notificações.

As funcionalidades administrativas relacionadas ao gerenciamento de tutores e veterinários não são apresentadas para esse perfil.

### VETERINARIO

O perfil **VETERINARIO** possui acesso ampliado ao sistema, incluindo:

- Dashboard;
- Agenda;
- Tutores;
- Veterinários;
- Anexos;
- Notificações.

O controle de acesso é aplicado tanto na interface quanto nas rotas protegidas da API.

---

# 🖥️ Interface Web — SuperVet

Além da API REST, o projeto possui uma interface web integrada ao backend.

O frontend é apresentado visualmente com o nome:

# SuperVet

O nome **ClyvoVet** continua sendo utilizado no backend e na estrutura principal do projeto.

A interface foi desenvolvida utilizando:

- Thymeleaf;
- HTML;
- CSS;
- JavaScript;
- Fetch API.

O frontend se comunica diretamente com os endpoints REST do Spring Boot.

---

# 🏠 Dashboard

Após realizar a autenticação, o usuário é direcionado para o dashboard do **SuperVet**.

O conteúdo exibido é adaptado de acordo com o perfil autenticado.

### Tutor

```text
Dashboard
├── Agenda
├── Anexos
└── Notificações
```

### Veterinário

```text
Dashboard
├── Agenda
├── Tutores
├── Veterinários
├── Anexos
└── Notificações
```

O sistema também possui funcionalidade de **logout**, responsável por remover os dados de autenticação armazenados no navegador e retornar o usuário para a tela de login.

---

# 📚 Entidades

A aplicação possui gerenciamento das seguintes entidades:

- Estado
- Cidade
- Bairro
- Usuário
- Endereço
- Tutor
- Veterinário
- Agenda
- Notificação
- Anexo

---

# 🔄 Operações CRUD

As principais entidades possuem operações completas de CRUD.

### CREATE

Permite cadastrar novos registros.

```http
POST
```

### READ

Permite consultar os registros existentes.

```http
GET
```

### UPDATE

Permite atualizar registros existentes.

```http
PUT
```

### DELETE

Permite remover registros.

```http
DELETE
```

As operações podem ser realizadas através do Swagger, Postman ou diretamente pela interface web do SuperVet.

---

# 🔎 Funcionalidades Além do CRUD

A aplicação não se limita às operações básicas de cadastro, consulta, atualização e exclusão.

Também foram desenvolvidas funcionalidades como:

- Busca de estado por UF;
- Busca de cidade por nome;
- Busca de bairro por nome;
- Busca de usuário por email;
- Busca de endereço por CEP;
- Busca de tutor por CPF;
- Busca de agenda por período;
- Busca de notificação por status de leitura;
- Busca de anexo por tipo de arquivo;
- Paginação de resultados;
- Validação de dados;
- Tratamento global de exceções;
- DTOs de Request e Response;
- Mappers;
- Links HATEOAS;
- Autenticação JWT;
- Autorização baseada em perfil;
- Integração entre frontend e API.

---

# 📦 Estrutura do Backend

O projeto utiliza uma arquitetura organizada em camadas.

```text
src/main/java
│
├── controller
├── dto
├── entity
├── exception
├── mapper
├── repository
├── security
└── service
```

### Controller

Responsável por disponibilizar os endpoints REST.

### DTO

Responsável pela transferência de dados entre cliente e servidor.

São utilizados DTOs de:

```text
Request
Response
```

### Entity

Representa as entidades persistidas no banco de dados.

### Mapper

Responsável pela conversão entre:

```text
Entity ↔ DTO
```

### Repository

Responsável pela comunicação com o banco de dados através do Spring Data JPA.

### Service

Contém as regras de negócio da aplicação.

### Security

Contém os componentes responsáveis pela autenticação e autorização utilizando Spring Security e JWT.

---

# 🎨 Estrutura do Frontend

Os arquivos da interface estão organizados dentro de:

```text
src/main/resources
│
├── static
│   ├── css
│   │   ├── dashboard.css
│   │   └── style.css
│   │
│   ├── img
│   │
│   └── js
│       ├── agenda.js
│       ├── anexos.js
│       ├── dashboard.js
│       ├── login.js
│       ├── notificacoes.js
│       ├── tutores.js
│       └── veterinarios.js
│
└── templates
    ├── agenda.html
    ├── anexos.html
    ├── dashboard.html
    ├── login.html
    ├── notificacoes.html
    ├── tutores.html
    └── veterinarios.html
```

---

# 🌐 Páginas Web

Com o projeto executando localmente, as principais páginas podem ser acessadas através das seguintes rotas:

```text
/login

/web/dashboard

/web/agenda

/web/tutores

/web/veterinarios

/web/anexos

/web/notificacoes
```

Algumas páginas são disponibilizadas de acordo com o perfil do usuário autenticado.

---

# 🔑 Endpoint de Login

A autenticação é realizada através de:

```http
POST /auth/login
```

Exemplo de requisição:

```json
{
  "email": "usuario@email.com",
  "senha": "senha-do-usuario"
}
```

Após a autenticação, a API retorna as informações necessárias para acesso ao sistema, incluindo o token JWT e o tipo de usuário.

> Não adicione credenciais reais ou tokens JWT ao repositório.

---

# 📖 Swagger / OpenAPI

O projeto utiliza **Swagger/OpenAPI** para documentação e testes dos endpoints.

Com a aplicação executando localmente:

```text
http://localhost:8080/swagger-ui/index.html
```

No Swagger é possível:

- Visualizar os endpoints;
- Consultar os métodos HTTP;
- Analisar os DTOs;
- Enviar requisições;
- Testar os CRUDs;
- Testar endpoints protegidos;
- Utilizar autenticação JWT.

---

# 🧪 Postman

A collection do Postman contendo os endpoints da aplicação pode ser acessada abaixo:

[Collection Postman](https://rm561802-3342259.postman.co/workspace/Maria-Luiza's-Workspace~ac5f82b0-f3ee-4446-8623-b6750534d59b/collection/55091734-6fa414e0-7523-4c60-811e-ff657fd5dae6?action=share&source=copy-link&creator=55091734)

O Postman pode ser utilizado para testar:

```text
GET
POST
PUT
DELETE
```

Também pode ser utilizado para testar as rotas protegidas utilizando o token JWT.

---

# 🗄️ Banco de Dados

O projeto utiliza **Oracle Database** como banco de dados relacional.

A persistência é realizada através de:

- Spring Data JPA;
- Hibernate.

Exemplo de configuração:

```properties
spring.datasource.url=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

> As credenciais reais do banco de dados não devem ser publicadas no GitHub.

---

# ▶️ Como Executar o Projeto

## 1. Clonar o repositório

```bash
git clone https://github.com/Malualvess-dev/ClyvoVet_Gradle.git
```

## 2. Entrar na pasta

```bash
cd ClyvoVet_Gradle
```

## 3. Configurar o banco

Configure as credenciais do Oracle no:

```text
src/main/resources/application.properties
```

## 4. Executar

No Windows:

```bash
gradlew.bat bootRun
```

No Linux/macOS:

```bash
./gradlew bootRun
```

A aplicação será iniciada, por padrão, em:

```text
http://localhost:8080
```

---

# 🧪 Fluxo Sugerido para Testes

Para validar o funcionamento completo da aplicação:

1. Inicie o Spring Boot.
2. Acesse `/login`.
3. Cadastre ou utilize um usuário existente.
4. Faça login.
5. Verifique a geração do token JWT.
6. Acesse o sistema utilizando a autenticação.
7. Verifique as funcionalidades disponíveis para o perfil.
8. Teste os cadastros.
9. Teste as consultas.
10. Teste as atualizações.
11. Teste as exclusões.
12. Faça logout.
13. Entre com outro perfil e verifique as diferenças de permissão.

---

# 🛡️ Segurança

Entre as medidas de segurança implementadas estão:

- Spring Security;
- JWT;
- BCrypt para senhas;
- Autenticação stateless;
- Rotas protegidas;
- Controle de acesso por perfil;
- Validação do token nas requisições;
- Logout com remoção dos dados de autenticação do navegador.

---

# 📋 Principais Recursos Implementados

- API REST;
- Interface web;
- Autenticação;
- JWT;
- Spring Security;
- BCrypt;
- Controle de acesso;
- Perfil Tutor;
- Perfil Veterinário;
- CRUD completo;
- Oracle Database;
- Spring Data JPA;
- Hibernate;
- DTOs;
- Mappers;
- Bean Validation;
- Tratamento de exceções;
- Paginação;
- HATEOAS;
- Swagger/OpenAPI;
- Postman;
- Thymeleaf;
- JavaScript integrado à API;
- Layout responsivo do SuperVet.

---

# 📁 Repositório

O código-fonte completo está disponível no GitHub:

https://github.com/Malualvess-dev/ClyvoVet_Gradle

---

# 👨‍💻 Integrantes

**Maria Luiza Alves de Aquino**  
RM561802

**João Victor Gomes de Souza**  
RM560907

**Lucas Barranha Giannini**  
RM564508

---

# 🐾 SuperVet

O **SuperVet** representa a interface web do projeto ClyvoVet, reunindo autenticação, gerenciamento de informações e controle de acesso em uma experiência única para tutores e veterinários.

O backend continua estruturado como **ClyvoVet API**, enquanto a experiência visual da aplicação é apresentada como **SuperVet**.

# ClyvoVet API

## Descrição

O ClyvoVet é uma API REST desenvolvida em Java com Spring Boot, criada para auxiliar tutores de pets no gerenciamento de informações importantes relacionadas aos cuidados com seus animais.

A aplicação permite cadastrar, consultar, atualizar e remover dados de usuários, tutores, endereços, agendas, notificações e anexos, utilizando persistência em banco de dados Oracle.

Além das operações básicas de CRUD, o projeto disponibiliza buscas específicas por parâmetros, paginação, ordenação, validações, tratamento de exceções e documentação com Swagger/OpenAPI.

---

## Objetivo do Projeto

O objetivo da solução é centralizar informações relevantes para tutores de pets, facilitando o acesso a dados cadastrais, endereços, documentos anexados, compromissos agendados e notificações.

Dessa forma, a API contribui para uma melhor organização das informações e evita que dados importantes fiquem espalhados em diferentes locais.

---

## Tecnologias Utilizadas

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Oracle Database
- Gradle
- Bean Validation
- Swagger / OpenAPI
- Spring HATEOAS

---

## Funcionalidades

A API possui funcionalidades para gerenciamento das seguintes entidades:

- Estado
- Cidade
- Bairro
- Usuário
- Endereço
- Tutor
- Agenda
- Notificação
- Anexo

---

## Funcionalidades além do CRUD

A aplicação não se limita apenas às operações básicas de cadastro, listagem, atualização e remoção.

Foram implementadas consultas específicas para facilitar o uso da API:

- Busca de estado por UF
- Busca de cidade por nome
- Busca de bairro por nome
- Busca de usuário por email
- Busca de endereço por CEP
- Busca de tutor por CPF
- Busca de agenda por período
- Busca de notificação por status de leitura
- Busca de anexo por tipo de arquivo

Também foram implementados:

- Paginação de resultados
- Ordenação de resultados
- Validação de campos com Bean Validation
- Tratamento global de exceções
- DTOs de Request e Response
- Links HATEOAS nas respostas
- Documentação dos endpoints com Swagger

---
Swagger

Com a aplicação em execução, a documentação da API pode ser acessada em:http://localhost:8080/swagger-ui/index.html

## Banco de Dados

O projeto utiliza Oracle Database como SGBD relacional.

A persistência dos dados é feita com Spring Data JPA e Hibernate.

Exemplo de configuração no `application.properties`:

---
POSTMAN
[Uploading ClyvoVet_API.postman_collection.json…]()


```properties
spring.datasource.url=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
spring.datasource.username=SERA ENVIADO PARA O PROFESSOR
spring.datasource.password=SERA ENVIADO PARA O PROFESSOR
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true


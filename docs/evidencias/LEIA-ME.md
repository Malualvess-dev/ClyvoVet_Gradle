# Evidências da entrega

Salve aqui os prints com exatamente estes nomes (o README já aponta para eles).
Não mostre senhas, client secrets nem a tela de variáveis com os valores visíveis.

| Arquivo | O que mostrar |
|---|---|
| `01-recursos-azure.png` | Portal da Azure com o grupo `rg-clyvovet`: plano `plan-clyvovet` e Web App `clyvovet-rm561802` |
| `02-variaveis-webapp.png` | Web App → Configurações → Variáveis de ambiente, com os **nomes** (SPRING_PROFILES_ACTIVE, DB_URL...) e os valores ocultos |
| `03-deploy.png` | Terminal com `az webapp deploy` terminando em "Deployment has completed successfully" |
| `04-flyway-oracle.png` | SQL Developer: `SELECT "version", "description", "success" FROM "flyway_schema_history";` |
| `05-tabelas-oracle.png` | SQL Developer: lista de tabelas TB_* do usuário |
| `06-app-online.png` | Site online logado (https://clyvovet-rm561802.azurewebsites.net) |
| `07-procedures.png` | Área do veterinário → Banco de dados, com o JSON dos pets e o relatório da coleira |
| `08-app-mobile.png` | App SuperVet listando os pets do tutor (API da Azure) |
| `09-app-e-site.png` | Uma consulta criada no app aparecendo na agenda do veterinário no site |

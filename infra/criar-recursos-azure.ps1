# =============================================================================
# ClyvoVet - Criação dos recursos na Azure via Azure CLI
#
# Cria: Grupo de recursos, Plano do App Service (Linux) e Web App Java 21,
# já configurado para usar o Oracle da FIAP (perfil "oracle").
#
# Pré-requisitos:
#   1. Azure CLI instalado e login feito com:  az login
#   2. Variáveis de ambiente definidas NA SESSÃO do PowerShell (não ficam no Git):
#        $env:DB_USERNAME="rmXXXXXX"
#        $env:DB_PASSWORD="sua-senha"
#        $env:GITHUB_CLIENT_ID="..."      $env:GITHUB_CLIENT_SECRET="..."
#        $env:GOOGLE_CLIENT_ID="..."      $env:GOOGLE_CLIENT_SECRET="..."
#
# Uso:  .\infra\criar-recursos-azure.ps1
# =============================================================================

$ErrorActionPreference = "Stop"

# ---- Nomes dos recursos -----------------------------------------------------
$RESOURCE_GROUP = "rg-clyvovet"
$LOCATION       = "eastus2"                 # troque se a assinatura bloquear a região
$APP_PLAN       = "plan-clyvovet"
$WEB_APP        = "clyvovet-rm561802"       # precisa ser único na Azure inteira
$SKU            = "B1"
$RUNTIME        = "JAVA:21-java21"
$DB_URL         = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL"

foreach ($variavel in "DB_USERNAME", "DB_PASSWORD") {
    if (-not (Test-Path "env:$variavel")) {
        throw "Defina `$env:$variavel antes de rodar o script."
    }
}

# ---- 1. Grupo de recursos ---------------------------------------------------
Write-Host "1/4 Criando o grupo de recursos $RESOURCE_GROUP..."
az group create `
    --name $RESOURCE_GROUP `
    --location $LOCATION

# ---- 2. Plano do App Service (Linux) ----------------------------------------
Write-Host "2/4 Criando o plano $APP_PLAN ($SKU, Linux)..."
az appservice plan create `
    --name $APP_PLAN `
    --resource-group $RESOURCE_GROUP `
    --location $LOCATION `
    --is-linux `
    --sku $SKU

# ---- 3. Web App Java 21 -----------------------------------------------------
Write-Host "3/4 Criando o Web App $WEB_APP ($RUNTIME)..."
az webapp create `
    --name $WEB_APP `
    --resource-group $RESOURCE_GROUP `
    --plan $APP_PLAN `
    --runtime $RUNTIME

az webapp config set `
    --name $WEB_APP `
    --resource-group $RESOURCE_GROUP `
    --always-on true `
    --http20-enabled true

az webapp update `
    --name $WEB_APP `
    --resource-group $RESOURCE_GROUP `
    --https-only true

# ---- 4. Configurações da aplicação (variáveis de ambiente) -----------------
# Os segredos vêm das variáveis da sessão: nada sensível fica neste arquivo.
Write-Host "4/4 Configurando as variáveis de ambiente do Web App..."
az webapp config appsettings set `
    --name $WEB_APP `
    --resource-group $RESOURCE_GROUP `
    --settings `
        SPRING_PROFILES_ACTIVE=oracle `
        SERVER_PORT=80 `
        SERVER_FORWARD_HEADERS_STRATEGY=framework `
        DB_URL=$DB_URL `
        DB_USERNAME=$env:DB_USERNAME `
        DB_PASSWORD=$env:DB_PASSWORD `
        GITHUB_CLIENT_ID=$env:GITHUB_CLIENT_ID `
        GITHUB_CLIENT_SECRET=$env:GITHUB_CLIENT_SECRET `
        GOOGLE_CLIENT_ID=$env:GOOGLE_CLIENT_ID `
        GOOGLE_CLIENT_SECRET=$env:GOOGLE_CLIENT_SECRET `
    --output none

Write-Host ""
Write-Host "Recursos criados. Endereço da aplicação: https://$WEB_APP.azurewebsites.net"

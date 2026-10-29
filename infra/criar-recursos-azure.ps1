# =============================================================================
# ClyvoVet - Criacao dos recursos na Azure via Azure CLI
#
# Cria: Grupo de recursos, Plano do App Service (Linux) e Web App Java 21,
# ja configurado para usar o Oracle da FIAP (perfil "oracle").
#
# Pre-requisitos:
#   1. Azure CLI instalado e login feito com:  az login
#   2. Variaveis de ambiente definidas NA SESSAO do PowerShell (nao ficam no Git):
#        $env:DB_USERNAME="rmXXXXXX"
#        $env:DB_PASSWORD="sua-senha"
#        $env:GITHUB_CLIENT_ID="..."      $env:GITHUB_CLIENT_SECRET="..."
#        $env:GOOGLE_CLIENT_ID="..."      $env:GOOGLE_CLIENT_SECRET="..."
#
# Uso:
#   powershell -ExecutionPolicy Bypass -File .\infra\criar-recursos-azure.ps1
#   powershell -ExecutionPolicy Bypass -File .\infra\criar-recursos-azure.ps1 -Location eastus
# =============================================================================

param(
    [string]$Location = "eastus"   # troque se a regiao estiver sem capacidade
)

$ErrorActionPreference = "Stop"

# ---- Nomes dos recursos -----------------------------------------------------
$RESOURCE_GROUP = "rg-clyvovet"
$GROUP_LOCATION = "eastus2"
$APP_PLAN       = "plan-clyvovet"
$WEB_APP        = "clyvovet-rm561802"       # precisa ser unico na Azure inteira
$SKU            = "B1"
$RUNTIME        = "JAVA:21-java21"
$DB_URL         = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL"

# Interrompe o script assim que um comando do Azure CLI falhar.
function Invoke-Az {
    az @args
    if ($LASTEXITCODE -ne 0) {
        throw "Falha no comando: az $($args -join ' ')"
    }
}

foreach ($variavel in "DB_USERNAME", "DB_PASSWORD") {
    if (-not (Test-Path "env:$variavel")) {
        throw "Defina `$env:$variavel antes de rodar o script."
    }
}

# ---- 1. Grupo de recursos ---------------------------------------------------
Write-Host "1/4 Criando o grupo de recursos $RESOURCE_GROUP..."
# O grupo guarda so os metadados; a regiao dele nao precisa ser a mesma do plano.
Invoke-Az group create `
    --name $RESOURCE_GROUP `
    --location $GROUP_LOCATION `
    --output none

# ---- 2. Plano do App Service (Linux) ----------------------------------------
Write-Host "2/4 Criando o plano $APP_PLAN ($SKU, Linux, $Location)..."
Invoke-Az appservice plan create `
    --name $APP_PLAN `
    --resource-group $RESOURCE_GROUP `
    --location $Location `
    --is-linux `
    --sku $SKU `
    --output none

# ---- 3. Web App Java 21 -----------------------------------------------------
Write-Host "3/4 Criando o Web App $WEB_APP ($RUNTIME)..."
Invoke-Az webapp create `
    --name $WEB_APP `
    --resource-group $RESOURCE_GROUP `
    --plan $APP_PLAN `
    --runtime $RUNTIME `
    --output none

Invoke-Az webapp config set `
    --name $WEB_APP `
    --resource-group $RESOURCE_GROUP `
    --always-on true `
    --http20-enabled true `
    --output none

Invoke-Az webapp update `
    --name $WEB_APP `
    --resource-group $RESOURCE_GROUP `
    --https-only true `
    --output none

# ---- 4. Configuracoes da aplicacao (variaveis de ambiente) ------------------
# Os segredos vem das variaveis da sessao: nada sensivel fica neste arquivo.
Write-Host "4/4 Configurando as variaveis de ambiente do Web App..."
Invoke-Az webapp config appsettings set `
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
Write-Host "Recursos criados com sucesso: https://$WEB_APP.azurewebsites.net"

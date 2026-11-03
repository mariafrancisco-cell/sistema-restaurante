# =============================================================================
# empacotar.ps1 - gera o zip de entrega do Sistema de Restaurante (Windows)
#
# Uso, no PowerShell, dentro da pasta do projeto:
#   powershell -ExecutionPolicy Bypass -File .\empacotar.ps1
#   powershell -ExecutionPolicy Bypass -File .\empacotar.ps1 -SemTestes
#
# O zip leva SO o que a entrega pede (lista $Arquivos abaixo).
# Fica de fora: target\, .idea\, *.iml, .git\ e qualquer outro arquivo solto.
# (Script sem acentos de proposito: o PowerShell antigo do Windows le mal UTF-8.)
# =============================================================================
param(
    [switch]$SemTestes
)

$ErrorActionPreference = "Stop"

$Nome = "sistema-restaurante"
$Zip  = "$Nome-entrega.zip"

# O que vai para a entrega. Para incluir mais alguma coisa, acrescente aqui.
$Arquivos = @("pom.xml", "src", "README.md", "Dockerfile", "docker-compose.yml",
              ".gitignore", ".dockerignore", ".gitattributes", "empacotar.sh", "empacotar.ps1")

function Aviso($msg) { Write-Host "[!] $msg" -ForegroundColor Yellow }
function Erro($msg)  { Write-Host "[ERRO] $msg" -ForegroundColor Red; exit 1 }

# Roda a partir da pasta onde o script esta (a raiz do projeto)
Set-Location -Path $PSScriptRoot

if (-not (Test-Path "pom.xml")) { Erro "pom.xml nao encontrado. Rode este script na pasta do projeto." }
if (-not (Test-Path "src"))     { Erro "Pasta src nao encontrada." }

# Apaga o zip antigo logo no comeco: se algo falhar, nao sobra um zip velho parecendo valido
$ZipCompleto = Join-Path $PSScriptRoot $Zip
if (Test-Path $ZipCompleto) { Remove-Item $ZipCompleto -Force }

# 1) README precisa ter o link do video
if (Select-String -Path "README.md" -Pattern "COLE O LINK AQUI" -Quiet) {
    Aviso "O README.md ainda nao tem o link do video (linha 'Video:')."
    $resposta = Read-Host "Continuar mesmo assim? (s/n)"
    if ($resposta -ne "s" -and $resposta -ne "S") {
        Erro "Cancelado. Cole o link no README.md e rode de novo."
    }
}

# 2) Testes + .jar executavel (bonus). Se algum teste falhar, o script para.
$Jar = $null
if ($SemTestes) {
    Aviso "Pulando testes e build (-SemTestes). O zip vai sem o .jar executavel."
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    Write-Host ">> Rodando os testes e gerando o .jar (mvn clean package)..."
    & mvn -q -B clean package
    if ($LASTEXITCODE -ne 0) { Erro "Build ou testes falharam. Corrija antes de entregar." }
    $Jar = Join-Path "target" "$Nome.jar"
    Write-Host ">> Testes OK." -ForegroundColor Green
} else {
    Aviso "Maven (mvn) nao encontrado no PATH. Rode os testes pela IDE antes de entregar."
    Aviso "O zip vai sem o .jar executavel."
}

# 3) Copia so os itens da lista para uma pasta temporaria
$Tmp = Join-Path ([System.IO.Path]::GetTempPath()) ([System.Guid]::NewGuid().ToString())
$Destino = Join-Path $Tmp $Nome
New-Item -ItemType Directory -Path $Destino -Force | Out-Null

try {
    foreach ($item in $Arquivos) {
        if (Test-Path $item) {
            Copy-Item -Path $item -Destination $Destino -Recurse -Force
        } else {
            Aviso "Nao encontrado (ignorado): $item"
        }
    }
    if ($Jar -and (Test-Path $Jar)) {
        Copy-Item -Path $Jar -Destination (Join-Path $Destino "$Nome.jar")
    }

    # 4) Cria o zip (recurso do proprio .NET, nao precisa instalar nada)
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    [System.IO.Compression.ZipFile]::CreateFromDirectory($Tmp, $ZipCompleto,
        [System.IO.Compression.CompressionLevel]::Optimal, $false)

    Write-Host ""
    Write-Host ">> Pronto: $Zip" -ForegroundColor Green
    Write-Host ">> Conteudo:"
    Get-ChildItem -Path $Destino -Recurse -File -Force | ForEach-Object {
        "   " + $Nome + $_.FullName.Substring($Destino.Length).Replace("\", "/")
    } | Sort-Object
}
finally {
    Remove-Item -Path $Tmp -Recurse -Force -ErrorAction SilentlyContinue
}

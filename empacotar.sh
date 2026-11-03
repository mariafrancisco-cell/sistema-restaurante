#!/usr/bin/env bash
# =============================================================================
# empacotar.sh — gera o zip de entrega do Sistema de Restaurante
#
# Uso (Linux, macOS ou Git Bash no Windows), na pasta do projeto:
#   bash empacotar.sh              -> roda os testes, gera o .jar e cria o zip
#   bash empacotar.sh --sem-testes -> só cria o zip (ex.: se não tiver Maven)
#
# O zip leva SÓ o que a entrega pede (lista "ARQUIVOS" abaixo).
# Fica de fora: target/, .idea/, *.iml, .git/ e qualquer outro arquivo solto.
# =============================================================================
set -e

NOME="sistema-restaurante"
ZIP="$NOME-entrega.zip"

# O que vai para a entrega. Para incluir mais alguma coisa, acrescente aqui.
ARQUIVOS="pom.xml src README.md Dockerfile docker-compose.yml .gitignore .dockerignore .gitattributes empacotar.sh empacotar.ps1"

# Roda a partir da pasta onde o script está (a raiz do projeto)
cd "$(dirname "$0")"

aviso() { echo "[!] $1"; }
erro()  { echo "[ERRO] $1"; exit 1; }

[ -f pom.xml ] || erro "pom.xml não encontrado. Rode este script na pasta do projeto."
[ -d src ]     || erro "Pasta src não encontrada."

# Apaga o zip antigo logo no começo: se algo falhar, não sobra um zip velho parecendo válido
rm -f "$ZIP"

# 1) README precisa ter o link do vídeo
if grep -q "COLE O LINK AQUI" README.md; then
    aviso "O README.md ainda não tem o link do vídeo (linha 'Vídeo:')."
    printf "Continuar mesmo assim? (s/n): "
    read -r resposta
    [ "$resposta" = "s" ] || [ "$resposta" = "S" ] || erro "Cancelado. Cole o link no README.md e rode de novo."
fi

# 2) Testes + .jar executável (bônus). Se algum teste falhar, o script para.
JAR=""
if [ "$1" = "--sem-testes" ]; then
    aviso "Pulando testes e build (--sem-testes). O zip vai sem o .jar executável."
elif command -v mvn >/dev/null 2>&1; then
    echo ">> Rodando os testes e gerando o .jar (mvn clean package)..."
    mvn -q -B clean package || erro "Build ou testes falharam. Corrija antes de entregar."
    JAR="target/$NOME.jar"
    echo ">> Testes OK."
else
    aviso "Maven (mvn) não encontrado no PATH. Rode os testes pela IDE antes de entregar."
    aviso "O zip vai sem o .jar executável."
fi

# 3) Copia só os itens da lista para uma pasta temporária
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
DESTINO="$TMP/$NOME"
mkdir -p "$DESTINO"

for item in $ARQUIVOS; do
    if [ -e "$item" ]; then
        cp -R "$item" "$DESTINO/"
    else
        aviso "Não encontrado (ignorado): $item"
    fi
done
[ -n "$JAR" ] && cp "$JAR" "$DESTINO/$NOME.jar"

# 4) Cria o zip. Usa o que existir na máquina: zip, jar (vem com o JDK) ou python.
RAIZ="$(pwd)"
( cd "$TMP"
  if command -v zip >/dev/null 2>&1; then
      zip -qr "$RAIZ/$ZIP" "$NOME"
  elif command -v jar >/dev/null 2>&1; then
      jar --create --no-manifest --file "$RAIZ/$ZIP" "$NOME"
  elif command -v python3 >/dev/null 2>&1; then
      python3 -m zipfile -c "$RAIZ/$ZIP" "$NOME"
  elif command -v python >/dev/null 2>&1; then
      python -m zipfile -c "$RAIZ/$ZIP" "$NOME"
  else
      erro "Não achei zip, jar nem python para criar o arquivo .zip."
  fi
)

echo ""
echo ">> Pronto: $ZIP"
echo ">> Conteúdo:"
( cd "$TMP" && find "$NOME" -type f | sort | sed 's/^/   /' )

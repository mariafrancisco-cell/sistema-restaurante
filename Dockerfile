# =============================================================================
# Dockerfile do Sistema de Restaurante
#
# "Multi-stage": são duas etapas (dois FROM).
#   1) build -> imagem grande, com Maven e JDK: compila, roda os testes e gera o .jar
#   2) final -> imagem pequena, só com o Java para RODAR (JRE) e o .jar pronto
# Assim a imagem final não carrega Maven, código-fonte nem dependências de teste.
# =============================================================================

# ---------- Estágio 1: build (compila e testa) ----------
FROM maven:3.9-eclipse-temurin-17 AS build

# Pasta de trabalho dentro do container (como um "cd /app")
WORKDIR /app

# Copia SÓ o pom.xml primeiro e baixa as dependências.
# Truque de cache: o Docker guarda cada passo em uma "camada". Enquanto o pom.xml
# não mudar, esta camada é reaproveitada e o Maven não baixa tudo de novo
# a cada alteração no código.
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Agora copia o código e gera o .jar.
# O "package" passa pela fase "test": se algum teste falhar, o build para aqui.
COPY src ./src
RUN mvn -B package

# ---------- Estágio 2: final (só roda o programa) ----------
FROM eclipse-temurin:17-jre AS final

# Garante acentos (ç, ã, é) corretos no terminal
ENV LANG=C.UTF-8

WORKDIR /app

# Pega do estágio "build" apenas o .jar gerado (target/sistema-restaurante.jar)
COPY --from=build /app/target/sistema-restaurante.jar .

# O sistema é interativo (lê o teclado com Scanner), por isso rode com:
#   docker compose run --rm restaurante
# -Dfile.encoding=UTF-8 faz o Java ler e escrever texto em UTF-8.
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "sistema-restaurante.jar"]

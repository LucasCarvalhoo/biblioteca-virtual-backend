# Dockerfile para Biblioteca API
FROM eclipse-temurin:17-jdk-alpine AS builder

# Metadados
LABEL maintainer="biblioteca-api@exemplo.com"
LABEL version="1.0"
LABEL description="API REST para gerenciamento de biblioteca digital"

# Diretório de trabalho
WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Dar permissão de execução ao mvnw
RUN chmod +x mvnw

# Baixar dependências (cache layer)
RUN ./mvnw dependency:go-offline -B

# Copiar código fonte
COPY src src

# Compilar aplicação
RUN ./mvnw clean package -DskipTests

# Etapa final - Runtime
FROM eclipse-temurin:17-jre-alpine

# Instalar curl para healthcheck
RUN apk --no-cache add curl

# Criar usuário não-root
RUN addgroup -g 1001 -S biblioteca && \
    adduser -S -D -H -u 1001 -h /app -s /sbin/nologin -G biblioteca -g biblioteca biblioteca

# Diretório de trabalho
WORKDIR /app

# Copiar JAR da etapa de build
COPY --from=builder /app/target/*.jar app.jar

# Alterar propriedade dos arquivos
RUN chown -R biblioteca:biblioteca /app

# Mudar para usuário não-root
USER biblioteca

# Expor porta
EXPOSE 8080

# Variáveis de ambiente
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Comandos para build e execução:
# docker build -t biblioteca-api .
# docker run -p 8080:8080 biblioteca-api
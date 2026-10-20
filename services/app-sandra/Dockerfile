# ---- Compilación ----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package -DskipTests

# ---- Ejecución ----
FROM openjdk:17-slim

ARG OTEL_AGENT_VERSION=2.1.0
ARG JMX_EXPORTER_VERSION=0.20.0

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar /opt/opentelemetry-javaagent.jar
ADD https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/${JMX_EXPORTER_VERSION}/jmx_prometheus_javaagent-${JMX_EXPORTER_VERSION}.jar /opt/jmx_prometheus_javaagent.jar
COPY jmx-config.yaml /opt/jmx-config.yaml
RUN chmod 644 /opt/*.jar /opt/jmx-config.yaml

COPY --from=build /build/target/app.jar /app/app.jar

EXPOSE 8080 9404

# El agente OTel se carga vía JAVA_TOOL_OPTIONS (docker-compose.yml);
# el JMX Exporter se carga aquí y publica métricas en :9404
ENTRYPOINT ["java", "-javaagent:/opt/jmx_prometheus_javaagent.jar=9404:/opt/jmx-config.yaml", "-jar", "/app/app.jar"]

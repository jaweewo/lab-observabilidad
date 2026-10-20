# Laboratorio de observabilidad SOAP/Java

Laboratorio de formación para practicar **trazas, métricas y logs** sobre una arquitectura
de servicios SOAP en Java (Spring Boot + Apache CXF), instrumentada con OpenTelemetry.

```
                 ┌─────────────────┐   ┌────────────────┐      ┌──────────────┐
  cliente SOAP ─▶│ app-orquestador │──▶│ fault-injector │─────▶│  app-gestor  │  gestor documental
                 │  (orquestador)  │   │   (proxy 9999) │─────▶│  app-inside  │  expediente electrónico
                 └──────┬──────────┘   └────────────────┘─────▶│  app-report  │  generación PDF
                        │ OTLP                                  └──────┬───────┘
                        ▼                                              │ OTLP
                 ┌──────────────┐  trazas   ┌────────┐                 │
                 │otel-collector│──────────▶│ Jaeger │◀────────────────┘
                 └──────┬───────┘           └────────┘
                        │ métricas :8889
                        ▼
  JMX :9404 ───▶ Prometheus ───┐
  logs Docker ─▶ Fluent Bit ─▶ Loki ─┴──▶ Grafana
```

| Servicio        | Puerto | Equivalente        | Operación SOAP                          |
|-----------------|--------|--------------------|-----------------------------------------|
| app-orquestador | 8080   | Orquestador        | `/ws/licencias` → `solicitarLicencia`   |
| app-gestor      | 8081   | Gestor documental  | `/ws/documentos` → `registrarDocumento` |
| app-report      | 8082   | ReportService      | `/ws/informes` → `generarInforme`       |
| app-inside      | 8083   | INSIDE             | `/ws/expedientes` → `crearExpediente`   |

Cada servicio simula una latencia aleatoria de 50–200 ms y devuelve una respuesta fija.
`app-orquestador` llama a los otros tres con `RestTemplate` **a través del fault-injector**, de modo
que los fallos inyectados se reflejan en el flujo completo.

## Requisitos

- Docker Engine 24+ con Docker Compose v2 (`docker compose`)
- `make`, `bash` y `curl` (en Windows: WSL2 o Git Bash)
- ~4 GB de RAM libres para los contenedores
- Acceso a Internet en el primer build (descarga de imágenes, dependencias Maven y javaagents)

> **Nota:** Fluent Bit lee los logs de `/var/lib/docker/containers`. Funciona en Linux y en
> Docker Desktop con backend WSL2; en otros entornos puede que haya que ajustar esa ruta.

## Puesta en marcha

```bash
# 1. Clonar
git clone https://github.com/<tu-usuario>/lab-observabilidad.git
cd lab-observabilidad

# 2. Descargar las imágenes base
docker compose pull --ignore-buildable

# 3. Construir los servicios Java y el fault-injector
make build

# 4. Arrancar
make up

# 5. Comprobar que todo responde (puede tardar ~1 min en arrancar las JVM)
./scripts/check-lab.sh

# 6. Generar tráfico y obtener un Trace ID
./scripts/smoke-test.sh
```

### Comandos `make`

| Comando       | Acción                                             |
|---------------|----------------------------------------------------|
| `make up`     | `docker compose up -d`                             |
| `make down`   | `docker compose down`                              |
| `make reset`  | `docker compose down -v && docker compose up -d`   |
| `make status` | Ejecuta `scripts/check-lab.sh`                     |
| `make logs`   | `docker compose logs -f`                           |
| `make build`  | `docker compose build`                             |

Para empezar totalmente de cero (borra volúmenes e imágenes de los servicios y reconstruye):
`./scripts/reset-lab.sh`.

## URLs

| Componente      | URL                                         | Descripción                                             |
|-----------------|---------------------------------------------|---------------------------------------------------------|
| Grafana         | http://localhost:3000                       | Dashboards, Explore de Loki/Prometheus/Jaeger (admin/admin, acceso anónimo habilitado) |
| Prometheus      | http://localhost:9090                       | Métricas OTel (vía collector) y JMX de cada JVM         |
| Jaeger          | http://localhost:16686                      | Búsqueda y visualización de trazas distribuidas        |
| app-orquestador      | http://localhost:8080/ws/licencias?wsdl     | WSDL del orquestador SOAP                               |
| fault-injector  | http://localhost:9999/fault                 | API de inyección de fallos                              |

En Grafana se aprovisiona el dashboard **Laboratorio → Lab Observabilidad — Visión general** y los
datasources Prometheus, Loki y Jaeger, enlazados entre sí: desde una línea de log se salta a su
traza (campo `trace_id`) y desde un span a sus logs.

## Fault injector

El fault-injector actúa de proxy (`/proxy/<servicio>/<ruta>`) delante de `app-gestor`,
`app-report` y `app-inside`. Por defecto no inyecta nada.

**Añadir 1,5 s de latencia al gestor documental:**

```bash
curl -X POST http://localhost:9999/fault \
  -H 'Content-Type: application/json' \
  -d '{"service": "app-gestor", "type": "latency", "ms": 1500}'
```

**Hacer fallar el 30 % de las llamadas a ReportService:**

```bash
curl -X POST http://localhost:9999/fault \
  -H 'Content-Type: application/json' \
  -d '{"service": "app-report", "type": "error", "rate": 0.3}'
```

**Consultar los fallos activos:**

```bash
curl http://localhost:9999/fault
```

**Desactivar todos los fallos:**

```bash
curl -X DELETE http://localhost:9999/fault
```

**Generar carga para observar el efecto:**

```bash
for i in $(seq 1 50); do ./scripts/smoke-test.sh > /dev/null; done
```

Después, observa en Jaeger los spans lentos o con error, en Grafana la subida de latencia p95 y
de errores 5xx, y en Loki los logs correlacionados por `trace_id`.

Para que `app-orquestador` llame directamente a los servicios sin pasar por el proxy, define en `.env`:

```
GESTOR_URL=http://app-gestor:8081
REPORT_URL=http://app-report:8082
INSIDE_URL=http://app-inside:8083
```

## Estructura

```
lab-observabilidad/
├── docker-compose.yml
├── Makefile
├── .env
├── services/            # app-orquestador, app-gestor, app-report, app-inside (Spring Boot + CXF)
├── fault-injector/      # Flask, puerto 9999
├── otel/                # configuración del OpenTelemetry Collector
├── fluent-bit/          # recogida de logs Docker → Loki
├── loki/
├── prometheus/
├── grafana/             # provisioning de datasources y dashboards
└── scripts/             # check-lab.sh, reset-lab.sh, smoke-test.sh
```

## Detalles de instrumentación

- **Trazas:** `opentelemetry-javaagent.jar` (cargado vía `JAVA_TOOL_OPTIONS`) → OTLP gRPC →
  collector → Jaeger. Cada servicio devuelve la cabecera `traceparent` en la respuesta.
- **Métricas:** el agente OTel exporta métricas HTTP/JVM por OTLP (expuestas por el collector en
  `:8889`); además `jmx_prometheus_javaagent.jar` expone los MBeans de la JVM y Tomcat en `:9404`.
- **Logs:** `LogstashEncoder` escribe JSON en stdout con `trace_id` y `span_id` del MDC
  (inyectados por el agente OTel). Fluent Bit los recoge y los envía a Loki con las etiquetas
  `job=docker`, `service` y `trace_id`.

> `trace_id` como etiqueta de Loki genera alta cardinalidad; se mantiene por simplicidad
> didáctica. En producción es preferible dejarlo solo en el contenido de la línea.

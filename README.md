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

| Servicio        | Puerto interno | Equivalente        | Operación SOAP                          |
|-----------------|----------------|--------------------|-----------------------------------------|
| app-orquestador | 8080 (publicado) | Orquestador      | `/ws/licencias` → `solicitarLicencia`   |
| app-gestor      | 8081           | Gestor documental | `/ws/documentos` → `registrarDocumento` |
| app-report      | 8082           | ReportService     | `/ws/informes` → `generarInforme`       |
| app-inside      | 8083           | INSIDE            | `/ws/expedientes` → `crearExpediente`   |

Cada servicio simula una latencia aleatoria de 50–200 ms y devuelve una respuesta fija.
`app-orquestador` llama a los otros tres con `RestTemplate` **a través del fault-injector**, de modo
que los fallos inyectados se reflejan en el flujo completo.

Los servicios backend no publican puertos en el host: se prueban mediante la llamada SOAP del
orquestador y se distinguen en la traza, las métricas y los logs. El ejercicio de fallos permite
inyectar latencia o errores específicamente en cada backend.

## Requisitos y preparación de la VM

- Ubuntu Server/Desktop 24.04 LTS en VirtualBox.
- Recomendado: 4 vCPU, 8 GB de RAM y 25 GB de disco dinámico. El mínimo práctico es
  4 GB libres para Docker; la compilación inicial también necesita memoria.
- Red VirtualBox **NAT** con reglas de reenvío de puertos para acceder desde el equipo anfitrión.
  No uses red en puente mientras estén habilitados los valores inseguros de Grafana descritos
  más abajo.
- Acceso a Internet durante la instalación y la primera compilación.

Instala Docker Engine desde el repositorio oficial, junto con Git, Make y utilidades:

```bash
sudo apt update
sudo apt install -y ca-certificates curl gnupg git make
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg \
  | sudo gpg --dearmor --yes -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu noble stable" \
  | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo systemctl enable --now docker
sudo usermod -aG docker "$USER"
```

Cierra sesión en Ubuntu y vuelve a entrar para que se aplique el grupo `docker`. Verifica:

```bash
docker --version
docker compose version
docker run --rm hello-world
```

En VirtualBox, en **Configuración → Red → Adaptador 1 → Avanzadas → Reenvío de puertos**,
añade reglas TCP con IP anfitrión `127.0.0.1` y los mismos puertos anfitrión/invitado:
`3000` (Grafana), `9090` (Prometheus), `16686` (Jaeger), `8080` (SOAP) y `9999` (fault-injector).
Si usas Ubuntu dentro de la propia VM, también puedes abrir esas direcciones en `localhost`.
No es necesario publicar puertos para Loki, Fluent Bit, el Collector ni los tres backends SOAP.

> **Seguridad:** este laboratorio configura Grafana con `admin/admin` y acceso anónimo con rol
> Admin. Es solo para una VM aislada y práctica local; no lo expongas a Internet ni a una red
> compartida. Antes de usar una interfaz de red en puente, cambia la contraseña `admin` en
> `docker-compose.yml` y desactiva `GF_AUTH_ANONYMOUS_ENABLED`.

> **Nota de logs:** Fluent Bit lee `/var/lib/docker/containers`, que corresponde a Docker Engine
> en Ubuntu con el driver de logs predeterminado. Si cambias `data-root`, usas Docker rootless o
> un driver distinto, tendrás que adaptar el volumen de Fluent Bit.

## Puesta en marcha

```bash
git clone https://github.com/jaweewo/lab-observabilidad.git
cd lab-observabilidad

# Comprueba la configuración y compila los cuatro servicios Java y el fault-injector.
docker compose config --quiet
make build

# Arranca el stack; las JVM pueden tardar alrededor de un minuto.
make up
./scripts/check-lab.sh

# Envía una solicitud SOAP y muestra el Trace ID para buscar en Jaeger.
./scripts/smoke-test.sh
```

El primer `make build` descarga imágenes base, dependencias Maven y agentes de instrumentación.
El resto de ejercicios reproducibles está en [ejercicios_practicos/](./ejercicios_practicos/);
el propósito y los límites de cada componente se explican en [summary.md](./summary.md).

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

| Componente     | URL                                     | Descripción                                                                 |
|----------------|-----------------------------------------|-----------------------------------------------------------------------------|
| Grafana        | http://localhost:3000                   | Dashboards y Explore de Loki/Prometheus/Jaeger (admin/admin, acceso anónimo) |
| Prometheus     | http://localhost:9090                   | Métricas OTel (vía Collector) y JMX de cada JVM                            |
| Jaeger         | http://localhost:16686                  | Búsqueda y visualización de trazas distribuidas                            |
| app-orquestador| http://localhost:8080/ws/licencias?wsdl | WSDL del orquestador SOAP                                                   |
| fault-injector | http://localhost:9999/fault             | API de inyección de fallos                                                  |

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

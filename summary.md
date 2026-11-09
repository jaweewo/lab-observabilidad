# Resumen del stack tecnológico

Este repositorio es un laboratorio reproducible de observabilidad para un flujo SOAP simulado.
Permite practicar correlación entre trazas, métricas y logs, además de observar fallos inducidos.
No implementa sistemas documentales reales ni produce documentos PDF reales: las respuestas de
los servicios backend son datos de ejemplo.

## Flujo de la aplicación

- **`app-orquestador`**: servicio Java con Spring Boot y Apache CXF. Publica el SOAP
  `solicitarLicencia` en `/ws/licencias`; coordina el registro documental, la creación de un
  expediente y la generación del informe.
- **`app-gestor`**: servicio SOAP simulado que registra el documento.
- **`app-inside`**: servicio SOAP simulado que crea el expediente electrónico.
- **`app-report`**: servicio SOAP simulado que devuelve una URL de ejemplo para un PDF.
- Los tres backends no publican puertos en el host. El orquestador los llama dentro de la red
  privada de Compose; así se practica un flujo distribuido sin exponer innecesariamente más
  endpoints.
- **`fault-injector`**: proxy en Flask. Enruta las llamadas a los backends y permite aplicar
  latencia fija o una tasa aleatoria de errores para un servicio. Su API de control se publica en
  `:9999`.

## Telemetría

- **OpenTelemetry Java Agent**: se carga en cada JVM mediante `JAVA_TOOL_OPTIONS`. Instrumenta
  automáticamente las peticiones HTTP/SOAP y la JVM, crea spans y propaga el contexto de traza
  entre servicios.
- **OpenTelemetry Collector**: recibe trazas y métricas por OTLP (gRPC/HTTP dentro de la red
  Compose). Exporta las trazas a Jaeger y expone las métricas para que Prometheus las recoja.
  Convierte atributos de recurso como `service.name` en etiquetas Prometheus `service_name`;
  esto permite separar series por servicio en el dashboard.
- **Jaeger**: almacena y visualiza trazas distribuidas. Permite seguir la solicitud de licencia
  desde el orquestador hasta los tres backends.
- **JMX Prometheus Java Agent**: cada JVM publica métricas de JVM y Tomcat en el puerto interno
  `9404`. Prometheus las scrapea con etiquetas del servicio.
- **Prometheus**: almacena las métricas de aplicación y JMX con retención configurada de siete
  días. Es el motor para consultar tasas de solicitudes, latencias, errores y uso de memoria.
- **Logback + LogstashEncoder**: las aplicaciones Java escriben eventos JSON en stdout,
  incluyendo `service`, `trace_id` y `span_id` en el contexto correspondiente.
- **Fluent Bit**: lee los archivos de logs del driver Docker `json-file`, extrae los campos JSON
  y reenvía eventos a Loki. Requiere el acceso de solo lectura al directorio
  `/var/lib/docker/containers` del host.
- **Loki**: almacena y consulta logs. Se configura `trace_id` como etiqueta para facilitar los
  ejercicios; esto tiene cardinalidad alta y no es una recomendación para un sistema productivo.
- **Grafana**: ofrece el dashboard provisionado, Explore y los datasources de Prometheus, Loki y
  Jaeger. Las fuentes incluyen enlaces de correlación entre logs y trazas.

## Ejecución y límites

- **Docker Compose** construye las aplicaciones y conecta los componentes en la red privada
  `lab-observabilidad`. Los datos de Loki, Prometheus y Grafana se conservan en volúmenes Docker.
- **Makefile y scripts** simplifican la compilación, el arranque, las comprobaciones y la emisión
  de una petición SOAP de prueba.
- Los backends devuelven resultados fijos y cada servicio introduce una pausa aleatoria corta.
  El inyector permite ampliar esa latencia o generar errores para enseñar diagnóstico.
- Es un stack didáctico local: las credenciales por defecto y el acceso anónimo administrador de
  Grafana no deben exponerse a redes no confiables.

Consulta la [guía de instalación y puesta en marcha](./README.md) y el índice de
[ejercicios prácticos](./ejercicios_practicos/README.md).

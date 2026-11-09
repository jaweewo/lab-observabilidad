# 15. Explorar nombres y etiquetas de métricas

## Objetivo

Descubrir las series exportadas y distinguir atributos de servicio de etiquetas de scrape.

## Pasos

En Prometheus, abre **Graph** y consulta:

```promql
http_server_request_duration_seconds_count
```

Después limita la consulta a un servicio:

```promql
http_server_request_duration_seconds_count{service_name="app-orquestador"}
```

Abre el menú de etiquetas de la interfaz y compara `service_name` con `job`. Consulta también
`jvm_memory_bytes_used{job="jmx"}`.

## Comprueba

- Las métricas HTTP incluyen el atributo `service_name` exportado desde el recurso OTel.
- `job` identifica el grupo de scrape de Prometheus; en JMX vale `jmx`.
- La conversión de atributos de recurso a etiquetas está configurada en
  `otel/collector-config.yaml`.

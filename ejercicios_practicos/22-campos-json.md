# 22. Analizar campos JSON de los logs

## Objetivo

Explorar los campos del evento JSON en Loki y diferenciar etiquetas de campos del contenido.

## Pasos

En Grafana Explore, ejecuta:

```logql
{job="docker", service="app-orquestador"} | json
```

Inspecciona los campos extraídos, como `level`, `message`, `trace_id` y `span_id`. Después filtra
por nivel:

```logql
{job="docker", service="app-orquestador"} | json | level="INFO"
```

## Comprueba

- `service` y `trace_id` se usan como etiquetas en esta configuración didáctica.
- `level`, `message` y otros campos se extraen del contenido JSON con `| json`.
- Un filtro por contenido se aplica después del selector de etiquetas.

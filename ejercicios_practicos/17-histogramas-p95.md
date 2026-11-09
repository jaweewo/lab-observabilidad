# 17. Interpretar histogramas y p95

## Objetivo

Calcular un percentil aproximado a partir de buckets del histograma de duración HTTP.

## Pasos

Genera tráfico durante un intervalo y consulta en Prometheus:

```promql
histogram_quantile(
  0.95,
  sum by (service_name, le) (
    rate(http_server_request_duration_seconds_bucket[1m])
  )
)
```

Compara la consulta con el panel **Latencia p95 servidor por servicio** del dashboard.
En Jaeger, abre algunas trazas y compara el tiempo total con las duraciones de sus spans.

## Comprueba

- El p95 es un percentil de la distribución en la ventana, no la duración de la última petición.
- La consulta conserva `le` al sumar buckets y agrupa por servicio.
- Con pocas muestras o tráfico irregular, el p95 puede estar vacío o ser inestable; genera más
  solicitudes antes de sacar conclusiones.

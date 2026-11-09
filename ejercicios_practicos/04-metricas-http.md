# 4. Tráfico y métricas HTTP en Prometheus

## Objetivo

Generar solicitudes SOAP y observar tasas, latencias y errores HTTP en Prometheus y Grafana.

## Pasos

Genera tráfico de prueba:

```bash
for i in $(seq 1 20); do ./scripts/smoke-test.sh >/dev/null; done
```

En Prometheus (`http://localhost:9090`) consulta:

```promql
sum by (service_name) (rate(http_server_request_duration_seconds_count[1m]))
```

```promql
histogram_quantile(
  0.95,
  sum by (service_name, le) (
    rate(http_server_request_duration_seconds_bucket[1m])
  )
)
```

Abre el dashboard **Lab Observabilidad — Visión general** en Grafana para comparar solicitudes
por segundo, p95 y errores 5xx por servicio. La métrica p95 requiere que haya suficientes
observaciones en la ventana seleccionada.

## Comprueba

- Aparecen series con `service_name` para cada servicio Java instrumentado.
- La tasa baja a cero cuando dejas de generar tráfico.
- El p95 de cada backend no es necesariamente igual: los servicios simulan latencias aleatorias
  de 50 a 200 ms, además de la latencia de orquestación y red.

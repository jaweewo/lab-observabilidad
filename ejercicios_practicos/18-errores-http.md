# 18. Observar las respuestas 5xx

## Objetivo

Consultar la tasa de respuestas HTTP 5xx y relacionarla con las solicitudes fallidas.

## Pasos

Primero genera una línea base de solicitudes correctas. Luego sigue el
[ejercicio de inyección de errores](07-errores.md) y ejecuta en Prometheus:

```promql
sum by (service_name) (
  rate(http_server_request_duration_seconds_count{
    http_response_status_code=~"5.."
  }[1m])
)
```

Compara la consulta con el panel **Errores HTTP 5xx (req/s)** del dashboard y abre en Jaeger una
traza fallida.

## Comprueba

- Las respuestas de error hacen aumentar la tasa de 5xx cuando hay suficientes muestras.
- La métrica se atribuye al servicio que produjo la respuesta HTTP registrada.
- Los errores SOAP y los códigos HTTP son señales relacionadas, pero no son lo mismo: inspecciona
  también el cuerpo SOAP y el span para entender la causa.

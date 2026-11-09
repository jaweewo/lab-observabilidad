# 24. Correlacionar logs y trazas en Grafana

## Objetivo

Usar los enlaces configurados entre Loki y Jaeger para pasar de un log a la traza relacionada.

## Pasos

1. Genera una solicitud SOAP y abre Grafana → **Explore** → Loki.
2. Busca un log de aplicación que tenga `trace_id`.
3. Abre el detalle de la línea y utiliza el enlace derivado **TraceID**.
4. En Jaeger, revisa los spans de la solicitud.
5. Vuelve a Jaeger y prueba el enlace a logs si está disponible para el span seleccionado.

## Comprueba

- Loki tiene un campo derivado que enlaza el `trace_id` con Jaeger.
- El datasource de Jaeger tiene configurada una consulta a Loki para buscar el Trace ID.
- La ventana temporal del enlace es de cinco minutos antes y después; amplíala si la petición queda
  fuera del rango.

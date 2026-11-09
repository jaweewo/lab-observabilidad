# 11. Buscar una traza en Jaeger

## Objetivo

Encontrar una solicitud en Jaeger usando el servicio y el intervalo temporal.

## Pasos

1. Ejecuta `./scripts/smoke-test.sh` y anota la hora aproximada.
2. Abre Jaeger en `http://localhost:16686`.
3. En **Search**, selecciona `app-orquestador` como servicio y un intervalo reciente.
4. Pulsa **Find Traces** y abre una de las trazas encontradas.

## Comprueba

- La traza corresponde al intervalo en que se ejecutó el script.
- El Trace ID coincide con el que imprimió `smoke-test.sh`.
- Se muestran spans de servidor y llamadas HTTP descendentes.

Si no aparece inmediatamente, espera unos segundos a que el Collector exporte el lote de spans y
actualiza la búsqueda.

# 13. Comparar Trace IDs entre solicitudes

## Objetivo

Verificar que solicitudes independientes generan trazas distintas y que las llamadas de una misma
solicitud se correlacionan.

## Pasos

Ejecuta dos veces el script:

```bash
./scripts/smoke-test.sh
./scripts/smoke-test.sh
```

Anota los Trace IDs y búscalos por separado en Jaeger. Compara los IDs de cada llamada con los
spans descendentes mostrados en su árbol.

## Comprueba

- Las dos solicitudes independientes tienen Trace IDs diferentes.
- Cada solicitud conserva un Trace ID común en sus spans distribuidos.
- El `trace_id` de los logs puede usarse para consultar eventos de esa misma solicitud en Loki.

No uses los IDs de las trazas como etiquetas de métricas: generan una cardinalidad excesiva para
Prometheus.

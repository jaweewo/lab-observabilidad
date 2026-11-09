# 5. Métricas de JVM por JMX

## Objetivo

Inspeccionar métricas JVM y distinguirlas de las métricas HTTP enviadas por OpenTelemetry.

## Pasos

En Prometheus ejecuta:

```promql
jvm_memory_bytes_used{job="jmx", area="heap"}
```

Para comprobar si todos los endpoints JMX están siendo scrapeados:

```promql
up{job="jmx"}
```

En Grafana, observa el panel **Heap JVM usado (JMX)** mientras generas solicitudes con
`./scripts/smoke-test.sh`.

## Comprueba

- Hay una serie de heap para cada servicio Java.
- Los targets de JMX están `up`.
- Cada JVM tiene un agente Prometheus JMX en el puerto interno `9404`; ese puerto no está publicado
  en el host.

El uso de heap puede no cambiar visiblemente con esta carga pequeña. El objetivo es familiarizarse
con la consulta y el scrape, no forzar presión de memoria.

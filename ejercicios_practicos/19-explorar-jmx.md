# 19. Explorar métricas JMX de la JVM

## Objetivo

Inspeccionar las métricas que el JMX Exporter extrae de las JVM.

## Pasos

En Prometheus, consulta:

```promql
jvm_memory_bytes_used{job="jmx"}
```

Filtra por heap y agrupa por servicio:

```promql
sum by (service) (jvm_memory_bytes_used{job="jmx", area="heap"})
```

Desde la página **Targets**, abre el endpoint de uno de los targets JMX para ver el formato
Prometheus expuesto en el puerto 9404.

## Comprueba

- Se observan series para más de una región de memoria y para distintos servicios.
- La etiqueta `area="heap"` identifica memoria heap.
- Las métricas JMX se recogen por separado de las métricas HTTP enviadas vía OTLP.

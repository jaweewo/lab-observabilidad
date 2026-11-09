# Ejercicios prácticos

Secuencia sugerida para una VM Ubuntu 24.04 con el laboratorio arrancado. La petición SOAP de
`app-orquestador` activa también `app-gestor`, `app-inside` y `app-report`; el dashboard muestra
por separado las métricas de cada servicio.

Antes de comenzar, sigue la sección de puesta en marcha del [README principal](../README.md).
En la VM, ejecuta los comandos desde la carpeta raíz del repositorio. Desde el anfitrión,
`localhost` apunta a la VM si configuraste las reglas de reenvío NAT documentadas.

## Itinerario

1. [Comprobación de servicios y WSDL](01-comprobacion.md)
2. [Petición SOAP y recorrido de la traza](02-soap-trazas.md)
3. [Logs JSON y correlación con Trace ID](03-logs-loki.md)
4. [Tráfico y métricas HTTP en Prometheus](04-metricas-http.md)
5. [Métricas de JVM por JMX](05-metricas-jvm.md)
6. [Inyección de latencia](06-latencia.md)
7. [Inyección de errores y recuperación](07-errores.md)
8. [Persistencia y parada limpia](08-ciclo-de-vida.md)
9. [Inspeccionar el contrato SOAP](09-contrato-soap.md)
10. [Interpretar la respuesta SOAP](10-respuesta-soap.md)
11. [Buscar una traza en Jaeger](11-busqueda-jaeger.md)
12. [Reconstruir el flujo entre servicios](12-flujo-servicios.md)
13. [Comparar Trace IDs entre solicitudes](13-trace-ids.md)
14. [Comprobar los targets de Prometheus](14-targets-prometheus.md)
15. [Explorar nombres y etiquetas de métricas](15-etiquetas-metricas.md)
16. [Calcular la tasa de solicitudes](16-tasa-solicitudes.md)
17. [Interpretar histogramas y p95](17-histogramas-p95.md)
18. [Observar las respuestas 5xx](18-errores-http.md)
19. [Explorar métricas JMX de la JVM](19-explorar-jmx.md)
20. [Comparar memoria JVM y tráfico](20-memoria-trafico.md)
21. [Filtrar logs por servicio en Loki](21-logs-por-servicio.md)
22. [Analizar campos JSON de los logs](22-campos-json.md)
23. [Encontrar el registro de un fallo inyectado](23-logs-fallos.md)
24. [Correlacionar logs y trazas en Grafana](24-correlacion-grafana.md)
25. [Explorar las fuentes y el dashboard de Grafana](25-grafana.md)
26. [Validar la API del fault-injector](26-api-fault-injector.md)
27. [Comparar latencia en distintos servicios](27-latencia-servicios.md)
28. [Combinar y limpiar fallos](28-fallos-combinados.md)
29. [Simular una dependencia fuera de servicio](29-dependencia-caida.md)
30. [Reto final: diagnóstico integral](30-diagnostico-integral.md)

> Ejecuta siempre el último paso para limpiar fallos configurados. No introduzcas datos reales:
> los identificadores de las solicitudes son ficticios.

# 25. Explorar las fuentes y el dashboard de Grafana

## Objetivo

Verificar las fuentes aprovisionadas y aprender qué información presenta el dashboard existente.

## Pasos

1. En Grafana, abre **Connections → Data sources** y comprueba Prometheus, Loki y Jaeger.
2. Abre **Dashboards → Laboratorio → Lab Observabilidad — Visión general**.
3. Cambia el intervalo temporal y genera nuevas solicitudes SOAP.
4. Abre cada panel y revisa la consulta asociada.
5. Usa **Explore** para repetir manualmente una consulta de Prometheus y una de Loki.

## Comprueba

- Prometheus alimenta las series HTTP y JMX; Loki aporta los logs.
- El panel de errores depende de que haya respuestas 5xx en el intervalo.
- Las fuentes se provisionan desde archivos del repositorio; una modificación manual del dashboard
  provisionado puede no persistir tras reiniciar Grafana.

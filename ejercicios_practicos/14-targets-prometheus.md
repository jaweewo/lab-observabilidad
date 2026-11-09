# 14. Comprobar los targets de Prometheus

## Objetivo

Identificar qué endpoints scrapea Prometheus y confirmar si están disponibles.

## Pasos

1. Abre `http://localhost:9090/targets`.
2. Revisa los grupos `prometheus`, `otel-collector` y `jmx`.
3. Confirma los cuatro targets del grupo JMX y abre uno de sus endpoints desde el enlace de la
   interfaz.
4. Ejecuta la consulta:

   ```promql
   up
   ```

## Comprueba

- Los cuatro targets JMX representan las JVM del orquestador y de sus tres backends.
- El Collector expone las métricas de aplicación en el puerto interno 8889.
- La etiqueta `up` vale `1` para un scrape correcto y `0` cuando falla.

Los targets están definidos en `prometheus/prometheus.yml`. No es necesario publicar el puerto
9404 en el anfitrión para que Prometheus scrapee los contenedores dentro de Compose.

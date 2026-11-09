# 3. Logs JSON y correlación con Trace ID

## Objetivo

Encontrar los logs de una solicitud SOAP en Loki y saltar desde un evento a su traza.

## Pasos

1. Genera una llamada y copia el Trace ID:

   ```bash
   ./scripts/smoke-test.sh
   ```

2. Abre Grafana → **Explore** → datasource **Loki**. Ejecuta:

   ```logql
   {job="docker", trace_id="<TRACE_ID>"}
   ```

   Sustituye `<TRACE_ID>` por el valor que imprimió el script.

3. Prueba también la consulta general:

   ```logql
   {job="docker", service=~"app-.*"}
   ```

4. Abre una línea de log y usa el campo derivado `TraceID` para navegar a Jaeger.

## Comprueba

- Los eventos JSON incluyen `service`; los eventos asociados a la petición pueden incluir
  `trace_id` y `span_id`.
- La etiqueta `service` distingue el orquestador y los servicios backend.
- La búsqueda por Trace ID encuentra eventos de varios servicios.

Si no aparece nada, espera el siguiente ciclo de Fluent Bit, comprueba
`docker compose logs fluent-bit loki` y verifica que el driver Docker usa la ruta predeterminada
de Ubuntu. La retención de Loki es de siete días.

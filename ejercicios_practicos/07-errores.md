# 7. Inyección de errores y recuperación

## Objetivo

Generar respuestas de error en una dependencia, correlacionarlas en la telemetría y recuperar el
estado normal del laboratorio.

## Pasos

1. Configura una tasa de error del 100 % para ReportService:

   ```bash
   curl -sS -X POST http://localhost:9999/fault \
     -H 'Content-Type: application/json' \
     -d '{"service":"app-report","type":"error","rate":1.0}'
   ```

2. Ejecuta una llamada SOAP:

   ```bash
   ./scripts/smoke-test.sh
   ```

   Esta llamada debe fallar mientras el inyector está activo. El script devuelve un código distinto
   de cero cuando recibe una respuesta no 2xx o no obtiene `traceparent`.

3. Busca la traza y los logs de la solicitud fallida. Observa el error en Jaeger, Grafana y
   Prometheus.
4. Desactiva los fallos y repite la llamada:

   ```bash
   curl -sS -X DELETE http://localhost:9999/fault
   ./scripts/smoke-test.sh
   ```

## Comprueba

- El error inyectado en un backend afecta a la respuesta SOAP del orquestador.
- La tasa de 5xx refleja los errores una vez que haya suficientes muestras en la ventana.
- Tras borrar los fallos, el flujo SOAP vuelve a responder con éxito.

La tasa `1.0` es determinista para esta práctica. Puedes repetirla con `0.3` y contar solicitudes
fallidas; a esa tasa el resultado de una muestra pequeña es aleatorio.

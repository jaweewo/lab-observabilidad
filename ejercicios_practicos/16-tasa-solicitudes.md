# 16. Calcular la tasa de solicitudes

## Objetivo

Usar un contador acumulativo para medir solicitudes por segundo y comparar servicios.

## Pasos

1. En Prometheus consulta:

   ```promql
   sum by (service_name) (
     rate(http_server_request_duration_seconds_count[1m])
   )
   ```

2. Ejecuta diez solicitudes:

   ```bash
   for i in $(seq 1 10); do ./scripts/smoke-test.sh >/dev/null; done
   ```

3. Espera al menos un ciclo de scrape y observa la consulta durante el siguiente minuto.
4. Deja de enviar solicitudes y observa cómo baja la tasa.

## Comprueba

- La tasa sube durante la generación de tráfico y cae cuando deja de haber peticiones.
- Una llamada SOAP genera actividad tanto en el orquestador como en sus dependencias.
- `rate` calcula cambios por segundo en una ventana; no representa el total histórico de llamadas.

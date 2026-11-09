# 8. Persistencia y parada limpia

## Objetivo

Reconocer qué datos sobreviven a la parada de los contenedores y cómo borrar el estado del
laboratorio intencionadamente.

## Pasos

1. Comprueba que no hay fallos activos:

   ```bash
   curl -sS http://localhost:9999/fault
   ```

2. Envía solicitudes SOAP y confirma que ves trazas, métricas y logs.
3. Para el stack sin borrar volúmenes:

   ```bash
   make down
   ```

4. Arranca de nuevo y espera a que los servicios estén listos:

   ```bash
   make up
   ./scripts/check-lab.sh
   ```

5. Observa qué datos persisten. Para borrar deliberadamente los datos almacenados:

   ```bash
   docker compose down -v
   make up
   ```

## Comprueba

- `make down` elimina contenedores y red, pero conserva los volúmenes con los datos de Loki,
  Prometheus y Grafana.
- `docker compose down -v` elimina también esos volúmenes: las métricas, logs, trazas de Jaeger
  (si están en memoria) y estado persistido de Grafana no deben tratarse como respaldo.
- El estado de fallos del inyector vive en memoria y se limpia al recrear su contenedor; usa
  `DELETE /fault` para una limpieza explícita.

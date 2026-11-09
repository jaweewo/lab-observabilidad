# 6. Inyección de latencia

## Objetivo

Ver cómo una demora en un servicio descendente afecta a la solicitud SOAP, los spans y el p95.

## Pasos

1. Genera varias peticiones de referencia y observa el p95 en Grafana.
2. Añade 1,5 segundos al servicio gestor documental:

   ```bash
   curl -sS -X POST http://localhost:9999/fault \
     -H 'Content-Type: application/json' \
     -d '{"service":"app-gestor","type":"latency","ms":1500}'
   ```

3. Envía más tráfico:

   ```bash
   for i in $(seq 1 10); do ./scripts/smoke-test.sh >/dev/null; done
   ```

4. Compara los spans en Jaeger y el p95 en Grafana con la referencia.
5. Limpia el inyector:

   ```bash
   curl -sS -X DELETE http://localhost:9999/fault
   ```

## Comprueba

- El span de la llamada al gestor se alarga y la petición completa del orquestador también.
- Los logs y spans permiten localizar el tramo lento.
- El p95 de la ventana aumenta durante la prueba y vuelve a bajar tras quitar la latencia y esperar
  a que la ventana móvil se actualice.

# 29. Simular una dependencia fuera de servicio

## Objetivo

Distinguir un fallo de transporte por dependencia indisponible de un error inyectado por el proxy.

## Pasos

> Este ejercicio detiene temporalmente `app-report`. Ejecuta el paso de recuperación aunque el
> resultado de la llamada no sea el esperado.

1. Comprueba que no hay fallos activos:

   ```bash
   curl -sS -X DELETE http://localhost:9999/fault
   ```

2. Detén el backend:

   ```bash
   docker compose stop app-report
   ```

3. Envía una llamada SOAP y conserva el Trace ID si la respuesta lo incluye.
4. Revisa la respuesta, la traza, los logs del orquestador y los del fault-injector.
5. Restaura inmediatamente el backend:

   ```bash
   docker compose start app-report
   ./scripts/check-lab.sh
   ./scripts/smoke-test.sh
   ```

## Comprueba

- El proxy no puede contactar al backend y responde con un error de upstream (HTTP 502).
- El orquestador puede convertir el error descendente en un fallo de la solicitud SOAP.
- La recuperación del contenedor permite volver a completar el flujo.

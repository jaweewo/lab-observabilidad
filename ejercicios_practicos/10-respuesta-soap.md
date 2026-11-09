# 10. Interpretar la respuesta SOAP

## Objetivo

Examinar una respuesta SOAP completa y distinguir los valores simulados de la telemetría.

## Pasos

Envía una solicitud con el script:

```bash
./scripts/smoke-test.sh
```

En la respuesta, identifica el elemento `resultado`. Repite la llamada y compara el contenido.
Después consulta los logs del orquestador:

```bash
docker compose logs --since=5m app-orquestador
```

## Comprueba

- La respuesta contiene identificadores de documento, expediente y URL de informe de ejemplo.
- Los resultados de negocio son fijos en los servicios backend; las duraciones y los Trace IDs
  cambian entre solicitudes.
- La respuesta SOAP satisfactoria no sustituye a la inspección de la traza: úsala para comprobar
  el resultado funcional y Jaeger para investigar el recorrido.

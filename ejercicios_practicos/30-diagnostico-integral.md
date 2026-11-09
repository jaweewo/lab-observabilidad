# 30. Reto final: diagnóstico integral

## Objetivo

Investigar un incidente simulado sin empezar por los logs: combinar señales de métricas, trazas y
logs y redactar una conclusión verificable.

## Escenario

Se observa una subida del p95 y algunas solicitudes SOAP fallidas. El síntoma puede deberse a una
llamada descendente lenta, una respuesta 5xx o ambas.

## Pasos

1. Establece una línea base con solicitudes correctas y anota hora y latencia aproximada.
2. Configura 800 ms de latencia en `app-inside` y una tasa de error del 30 % en `app-report`.
3. Envía al menos 20 solicitudes; cuenta éxitos y fallos sin descartar los códigos de salida:

   ```bash
   ok=0
   failed=0
   for i in $(seq 1 20); do
     if ./scripts/smoke-test.sh >/dev/null 2>&1; then
       ok=$((ok + 1))
     else
       failed=$((failed + 1))
     fi
   done
   printf 'Éxitos: %s; fallos: %s\n' "$ok" "$failed"
   ```

4. Usa el dashboard para identificar el servicio con latencia y la tasa 5xx.
5. Abre una traza lenta y otra fallida en Jaeger; localiza el tramo que las diferencia.
6. Busca en Loki los eventos de al menos una solicitud fallida usando su Trace ID.
7. Escribe una conclusión con síntoma, evidencia, dependencia afectada y acción de recuperación.
8. Limpia los fallos y verifica una nueva solicitud satisfactoria:

   ```bash
   curl -sS -X DELETE http://localhost:9999/fault
   ./scripts/smoke-test.sh
   ```

## Entrega esperada

- Un Trace ID de una petición lenta y otro de una petición fallida.
- Una consulta PromQL y una consulta LogQL utilizadas durante el diagnóstico.
- Una explicación basada en spans que diferencie latencia adicional de error descendente.
- Confirmación de que el flujo volvió a funcionar después de la limpieza.

La tasa del 30 % es aleatoria; con 20 solicitudes no se garantiza exactamente un 30 % de fallos.

# 27. Comparar latencia en distintos servicios

## Objetivo

Aplicar la misma latencia artificial a distintos backends y observar dónde aparece el tiempo
adicional.

## Pasos

1. Genera una línea base con varias llamadas y observa p95 y spans.
2. Configura 1 segundo de latencia en `app-inside`:

   ```bash
   curl -sS -X POST http://localhost:9999/fault \
     -H 'Content-Type: application/json' \
     -d '{"service":"app-inside","type":"latency","ms":1000}'
   ```

3. Envía solicitudes y observa su traza.
4. Borra todos los fallos y repite la prueba con `app-report`.
5. Compara el span descendente afectado y la duración total del orquestador.

## Comprueba

- La latencia aparece en el tramo asociado al backend seleccionado.
- El tiempo total del orquestador incorpora los tiempos de las llamadas secuenciales.
- Al borrar los fallos y esperar a que se actualice la ventana de métricas, las nuevas trazas
  vuelven a la latencia base.

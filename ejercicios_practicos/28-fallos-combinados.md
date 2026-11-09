# 28. Combinar y limpiar fallos

## Objetivo

Aplicar más de un tipo de fallo y comprobar que la limpieza global los elimina.

## Pasos

1. Añade latencia al gestor:

   ```bash
   curl -sS -X POST http://localhost:9999/fault \
     -H 'Content-Type: application/json' \
     -d '{"service":"app-gestor","type":"latency","ms":500}'
   ```

2. Añade una tasa de error del 50 % al mismo servicio:

   ```bash
   curl -sS -X POST http://localhost:9999/fault \
     -H 'Content-Type: application/json' \
     -d '{"service":"app-gestor","type":"error","rate":0.5}'
   ```

3. Consulta `GET /fault`, genera varias solicitudes y observa latencia y errores.
4. Ejecuta `DELETE /fault`, confirma que el mapa queda vacío y repite una solicitud.

## Comprueba

- Las configuraciones de latencia y tasa de error pueden coexistir para el mismo servicio.
- La latencia se aplica antes de decidir si se inyecta el error aleatorio.
- `DELETE /fault` limpia todas las configuraciones activas, no solo el último servicio.

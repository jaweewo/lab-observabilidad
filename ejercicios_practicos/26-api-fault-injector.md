# 26. Validar la API del fault-injector

## Objetivo

Consultar el estado del proxy y observar cómo valida las solicitudes incorrectas.

## Pasos

1. Consulta los fallos activos:

   ```bash
   curl -i http://localhost:9999/fault
   ```

2. Envía una configuración para un servicio inexistente:

   ```bash
   curl -i -X POST http://localhost:9999/fault \
     -H 'Content-Type: application/json' \
     -d '{"service":"app-inventado","type":"latency","ms":500}'
   ```

3. Prueba una tasa fuera del intervalo permitido:

   ```bash
   curl -i -X POST http://localhost:9999/fault \
     -H 'Content-Type: application/json' \
     -d '{"service":"app-report","type":"error","rate":1.5}'
   ```

4. Comprueba el estado con `GET /fault` y limpia con `DELETE /fault`.

## Comprueba

- Las configuraciones válidas se aceptan con HTTP 201.
- Los valores inválidos se rechazan con HTTP 400 y un mensaje explicativo.
- La tasa de error permitida está entre 0.0 y 1.0; la latencia debe ser un entero no negativo.

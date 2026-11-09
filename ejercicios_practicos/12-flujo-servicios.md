# 12. Reconstruir el flujo entre servicios

## Objetivo

Usar el árbol de spans para explicar el orden de las dependencias de una solicitud de licencia.

## Pasos

1. Genera una llamada SOAP y localiza su traza en Jaeger.
2. Expande el árbol de spans y anota el nombre de los servicios, las operaciones y las duraciones.
3. Relaciona el recorrido con la orquestación: registro documental, creación de expediente y
   generación del informe.
4. Compara los nombres de servicio de Jaeger con `OTEL_SERVICE_NAME` en `docker-compose.yml`.

## Comprueba

- Las llamadas descendentes pertenecen a la misma traza distribuida.
- El orden de los spans concuerda con el flujo de `LicenciasService`.
- El servicio con mayor duración no tiene por qué ser el orquestador: las llamadas a dependencias
  también contribuyen a la duración total.

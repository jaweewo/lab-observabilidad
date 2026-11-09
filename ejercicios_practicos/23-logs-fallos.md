# 23. Encontrar el registro de un fallo inyectado

## Objetivo

Correlacionar el log del fault-injector con los eventos de la petición que recibió la latencia o el
error.

## Pasos

1. Configura latencia para `app-gestor` con el comando del
   [ejercicio 6](06-latencia.md).
2. Envía una solicitud y anota el Trace ID.
3. En Loki busca:

   ```logql
   {job="docker", service="fault-injector", trace_id="<TRACE_ID>"}
   ```

4. Busca también los logs Java de la misma traza:

   ```logql
   {job="docker", service=~"app-.*", trace_id="<TRACE_ID>"}
   ```

5. Borra la configuración del fault-injector al terminar.

## Comprueba

- El fault-injector registra eventos de activación que pueden incluir el Trace ID recibido.
- Los logs de aplicación y del proxy describen perspectivas distintas del mismo flujo.
- Si el primer filtro no encuentra resultados, revisa el selector amplio
  `{job="docker", service="fault-injector"}` y confirma que hubo una llamada mientras el fallo
  estaba activo.

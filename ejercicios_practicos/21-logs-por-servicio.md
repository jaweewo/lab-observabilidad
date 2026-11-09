# 21. Filtrar logs por servicio en Loki

## Objetivo

Usar etiquetas de Loki para acotar la búsqueda al servicio que interesa.

## Pasos

1. Genera una solicitud SOAP.
2. En Grafana → **Explore**, selecciona Loki y consulta:

   ```logql
   {job="docker", service="app-orquestador"}
   ```

3. Repite con `service="app-gestor"`, `service="app-inside"` y `service="app-report"`.
4. Prueba la consulta del dashboard:

   ```logql
   {job="docker", service=~"app-.*"}
   ```

## Comprueba

- Las etiquetas permiten filtrar por contenedor/servicio.
- Una solicitud distribuida puede generar logs en varios servicios.
- Si no hay resultados nuevos, espera a Fluent Bit y comprueba que la petición se haya completado.

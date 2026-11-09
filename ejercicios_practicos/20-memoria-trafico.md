# 20. Comparar memoria JVM y tráfico

## Objetivo

Comparar la evolución temporal de una métrica de JVM con la carga de solicitudes, sin confundir
correlación con causalidad.

## Pasos

1. En Grafana, abre el panel de heap JVM y anota los valores de partida.
2. Genera tráfico durante uno o dos minutos:

   ```bash
   for i in $(seq 1 30); do ./scripts/smoke-test.sh >/dev/null; done
   ```

3. Compara el panel de heap con el de solicitudes por segundo.
4. Consulta en Prometheus `sum by (service) (jvm_memory_bytes_used{job="jmx", area="heap"})`.

## Comprueba

- Las JVM pueden conservar memoria reservada o usada después de procesar las solicitudes.
- Esta carga pequeña quizá no produzca un cambio visible; eso no significa que el scrape JMX
  falle.
- No concluyas que el tráfico causa un aumento sostenido de memoria a partir de una sola prueba.

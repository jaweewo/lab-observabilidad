# 1. Comprobación de servicios y WSDL

## Objetivo

Validar el arranque del stack, descubrir los puntos de entrada accesibles y consultar el contrato
SOAP del orquestador.

## Pasos

Desde la raíz del repositorio:

```bash
docker compose ps
./scripts/check-lab.sh
curl -fsS 'http://localhost:8080/ws/licencias?wsdl' | head -n 25
curl -fsS http://localhost:9999/fault
```

En el navegador, abre Grafana (`http://localhost:3000`), Prometheus
(`http://localhost:9090`) y Jaeger (`http://localhost:16686`).

## Comprueba

- Los contenedores están en estado `Up` y pasan las comprobaciones del script.
- El WSDL describe `solicitarLicencia`.
- El fault-injector devuelve la lista de servicios y, de inicio, no tiene fallos activos.
- Prometheus y los demás componentes no se exponen en puertos separados para cada backend:
  app-gestor, app-inside y app-report son destinos internos del orquestador.

## Si algo falla

```bash
docker compose ps
docker compose logs --tail=100
```

Espera aproximadamente un minuto tras el primer arranque antes de repetir `./scripts/check-lab.sh`.

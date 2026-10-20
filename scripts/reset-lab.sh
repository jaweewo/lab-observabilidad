#!/usr/bin/env bash
# Reinicia el laboratorio desde cero: borra volúmenes e imágenes locales y reconstruye.
set -euo pipefail

cd "$(dirname "$0")/.."

echo ">> Parando el laboratorio y eliminando volúmenes..."
docker compose down -v

echo ">> Eliminando imágenes locales de los servicios app-*..."
for svc in app-orquestador app-gestor app-report app-inside; do
  docker image rm -f "lab-observabilidad/${svc}:latest" 2>/dev/null || true
done

echo ">> Reconstruyendo y arrancando..."
docker compose up -d --build

echo ">> Hecho. Comprueba el estado con: make status"

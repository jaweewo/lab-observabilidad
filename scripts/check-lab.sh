#!/usr/bin/env bash
# Comprueba que los componentes del laboratorio responden.
set -u

GREEN='\033[0;32m'
RED='\033[0;31m'
BOLD='\033[1m'
NC='\033[0m'

failures=0

check() {
  local name="$1" url="$2" code
  code=$(curl -s -o /dev/null -w '%{http_code}' --max-time 5 "$url")
  if [[ "$code" =~ ^[23] ]]; then
    printf "  ${GREEN}[ OK ]${NC}  %-16s %-45s (HTTP %s)\n" "$name" "$url" "$code"
  else
    printf "  ${RED}[FAIL]${NC}  %-16s %-45s (HTTP %s)\n" "$name" "$url" "$code"
    failures=$((failures + 1))
  fi
}

printf "${BOLD}Estado del laboratorio de observabilidad${NC}\n"
check "Grafana"         "http://localhost:3000/api/health"
check "Prometheus"      "http://localhost:9090/-/ready"
check "Jaeger"          "http://localhost:16686/"
check "app-orquestador" "http://localhost:8080/ws/licencias?wsdl"
check "fault-injector"  "http://localhost:9999/fault"

echo
if [[ $failures -eq 0 ]]; then
  printf "${GREEN}Todos los componentes responden.${NC}\n"
else
  printf "${RED}%d componente(s) no responden.${NC} Revisa 'docker compose ps' y 'make logs'.\n" "$failures"
fi
exit "$failures"

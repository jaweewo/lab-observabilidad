#!/usr/bin/env bash
# Envía una solicitud de licencia SOAP a app-orquestador y muestra el Trace ID.
set -euo pipefail

URL="${ORQUESTADOR_URL:-http://localhost:8080}/ws/licencias"

read -r -d '' SOAP_REQUEST <<'EOF' || true
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:lic="http://lab.es/orquestador">
  <soapenv:Header/>
  <soapenv:Body>
    <lic:solicitarLicencia>
      <idSolicitante>12345678Z</idSolicitante>
      <tipoLicencia>OBRA_MENOR</tipoLicencia>
    </lic:solicitarLicencia>
  </soapenv:Body>
</soapenv:Envelope>
EOF

headers=$(mktemp)
body=$(mktemp)
trap 'rm -f "$headers" "$body"' EXIT

echo "POST $URL"
status=$(curl -s -o "$body" -D "$headers" -w '%{http_code}' \
  -H 'Content-Type: text/xml; charset=utf-8' \
  -H 'SOAPAction: ""' \
  --data "$SOAP_REQUEST" \
  "$URL")

echo "HTTP $status"
echo "--- Respuesta ---"
cat "$body"
echo
echo "-----------------"

traceparent=$(grep -i '^traceparent:' "$headers" | tr -d '\r' | awk '{print $2}' || true)
if [[ -z "$traceparent" ]]; then
  echo "No se recibió la cabecera traceparent en la respuesta." >&2
  exit 1
fi

trace_id=$(echo "$traceparent" | cut -d- -f2)
echo "traceparent: $traceparent"
echo "Trace ID:    $trace_id"
echo "Jaeger:      http://localhost:16686/trace/$trace_id"

[[ "$status" =~ ^2 ]]

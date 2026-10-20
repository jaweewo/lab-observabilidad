"""
Fault injector del laboratorio.

API de control:
  POST   /fault   {"service": "app-evacsv", "type": "latency", "ms": 1500}
                  {"service": "app-report", "type": "error",   "rate": 0.3}
  DELETE /fault   desactiva todos los fallos
  GET    /fault   estado actual

Proxy:
  /proxy/<service>/<path>  reenvía la petición al servicio real aplicando el fallo
                           configurado (sleep para latencia, excepción aleatoria
                           para la tasa de error).
"""
import json
import logging
import os
import random
import sys
import threading
import time

import requests
from flask import Flask, Response, jsonify, request

SERVICES = {
    "app-sandra": os.getenv("APP_SANDRA_URL", "http://app-sandra:8080"),
    "app-evacsv": os.getenv("APP_EVACSV_URL", "http://app-evacsv:8081"),
    "app-report": os.getenv("APP_REPORT_URL", "http://app-report:8082"),
    "app-inside": os.getenv("APP_INSIDE_URL", "http://app-inside:8083"),
}

# Cabeceras hop-by-hop (y las que requests recalcula) que no se reenvían
EXCLUDED_HEADERS = {
    "connection", "keep-alive", "proxy-authenticate", "proxy-authorization",
    "te", "trailers", "transfer-encoding", "upgrade", "host",
    "content-length", "content-encoding",
}

SOAP_FAULT = """<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <soap:Fault>
      <faultcode>soap:Server</faultcode>
      <faultstring>{message}</faultstring>
    </soap:Fault>
  </soap:Body>
</soap:Envelope>"""


class JsonFormatter(logging.Formatter):
    """Logs en JSON para que Fluent Bit los indexe igual que los de las apps Java."""

    def format(self, record):
        entry = {
            "@timestamp": time.strftime("%Y-%m-%dT%H:%M:%S", time.gmtime(record.created))
                          + f".{int(record.msecs):03d}Z",
            "level": record.levelname,
            "logger_name": record.name,
            "message": record.getMessage(),
            "service": "fault-injector",
        }
        traceparent = getattr(record, "traceparent", None)
        if traceparent:
            parts = traceparent.split("-")
            if len(parts) == 4:
                entry["trace_id"] = parts[1]
        return json.dumps(entry, ensure_ascii=False)


handler = logging.StreamHandler(sys.stdout)
handler.setFormatter(JsonFormatter())
log = logging.getLogger("fault-injector")
log.addHandler(handler)
log.setLevel(logging.INFO)
log.propagate = False

app = Flask(__name__)

_lock = threading.Lock()
_faults = {}  # service -> {"latency_ms": int, "error_rate": float}


class InjectedFault(Exception):
    pass


def _bad_request(message):
    return jsonify({"error": message}), 400


# --------------------------------------------------------------------- API control

@app.get("/fault")
def get_faults():
    with _lock:
        return jsonify({"faults": dict(_faults), "services": sorted(SERVICES)})


@app.post("/fault")
def set_fault():
    body = request.get_json(silent=True) or {}
    service = body.get("service")
    fault_type = body.get("type")

    if service not in SERVICES:
        return _bad_request(f"service debe ser uno de {sorted(SERVICES)}")
    if fault_type not in ("latency", "error"):
        return _bad_request('type debe ser "latency" o "error"')

    with _lock:
        fault = _faults.setdefault(service, {"latency_ms": 0, "error_rate": 0.0})
        if fault_type == "latency":
            try:
                ms = int(body.get("ms", 1000))
            except (TypeError, ValueError):
                return _bad_request("ms debe ser un entero")
            if ms < 0:
                return _bad_request("ms debe ser >= 0")
            fault["latency_ms"] = ms
        else:
            try:
                rate = float(body.get("rate", 0.5))
            except (TypeError, ValueError):
                return _bad_request("rate debe ser un número")
            if not 0.0 <= rate <= 1.0:
                return _bad_request("rate debe estar entre 0.0 y 1.0")
            fault["error_rate"] = rate
        current = dict(fault)

    log.info("Fallo activado en %s: %s", service, current)
    return jsonify({"service": service, "fault": current}), 201


@app.delete("/fault")
def clear_faults():
    with _lock:
        _faults.clear()
    log.info("Todos los fallos desactivados")
    return jsonify({"faults": {}})


# --------------------------------------------------------------------------- Proxy

@app.route("/proxy/<service>/", defaults={"path": ""},
           methods=["GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"])
@app.route("/proxy/<service>/<path:path>",
           methods=["GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"])
def proxy(service, path):
    if service not in SERVICES:
        return jsonify({"error": f"servicio desconocido: {service}"}), 404

    traceparent = request.headers.get("traceparent")
    extra = {"traceparent": traceparent}

    with _lock:
        fault = dict(_faults.get(service, {}))

    latency_ms = fault.get("latency_ms", 0)
    if latency_ms:
        log.info("Inyectando %d ms de latencia en %s", latency_ms, service, extra=extra)
        time.sleep(latency_ms / 1000.0)

    error_rate = fault.get("error_rate", 0.0)
    if error_rate and random.random() < error_rate:
        raise InjectedFault(f"Fallo inyectado en {service} (rate={error_rate})")

    upstream = f"{SERVICES[service]}/{path}"
    headers = {k: v for k, v in request.headers if k.lower() not in EXCLUDED_HEADERS}
    try:
        resp = requests.request(
            request.method, upstream,
            params=request.args, data=request.get_data(), headers=headers,
            timeout=30, allow_redirects=False,
        )
    except requests.RequestException as exc:
        log.error("Error contactando %s: %s", upstream, exc, extra=extra)
        return Response(SOAP_FAULT.format(message=f"Upstream no disponible: {service}"),
                        status=502, content_type="text/xml; charset=utf-8")

    out_headers = [(k, v) for k, v in resp.headers.items() if k.lower() not in EXCLUDED_HEADERS]
    return Response(resp.content, status=resp.status_code, headers=out_headers)


@app.errorhandler(InjectedFault)
def handle_injected_fault(exc):
    log.warning("%s", exc, extra={"traceparent": request.headers.get("traceparent")})
    return Response(SOAP_FAULT.format(message=str(exc)), status=500,
                    content_type="text/xml; charset=utf-8")


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=9999, threaded=True)

# 2. Petición SOAP y recorrido de la traza

## Objetivo

Enviar una solicitud SOAP real al endpoint del laboratorio y seguir su Trace ID en Jaeger.

## Pasos

```bash
./scripts/smoke-test.sh
```

El script imprime la respuesta, la cabecera `traceparent`, el Trace ID y un enlace directo a
Jaeger. Si quieres inspeccionar el sobre SOAP de entrada, usa:

```bash
curl -sS -D /tmp/soap-headers.txt -o /tmp/soap-response.xml \
  -H 'Content-Type: text/xml; charset=utf-8' \
  -H 'SOAPAction: ""' \
  --data-binary @- http://localhost:8080/ws/licencias <<'SOAP'
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:lic="http://lab.es/orquestador">
  <soapenv:Header/>
  <soapenv:Body>
    <lic:solicitarLicencia>
      <idSolicitante>12345678Z</idSolicitante>
      <tipoLicencia>OBRA_MENOR</tipoLicencia>
    </lic:solicitarLicencia>
  </soapenv:Body>
</soapenv:Envelope>
SOAP
grep -i '^traceparent:' /tmp/soap-headers.txt
cat /tmp/soap-response.xml
```

## Comprueba

En Jaeger, busca el servicio `app-orquestador` y abre la traza más reciente. Identifica los spans
HTTP/SOAP asociados al orquestador y a sus llamadas a gestor documental, INSIDE y ReportService.
Anota sus duraciones y el orden del flujo.

## Preguntas

- ¿Qué servicios aparecen bajo el mismo Trace ID?
- ¿Qué cambia en una traza si una llamada descendente falla?
- ¿Qué cabecera propaga el contexto de traza?

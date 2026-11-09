# 9. Inspeccionar el contrato SOAP

## Objetivo

Reconocer en el WSDL la operación, los parámetros y el endpoint SOAP que ofrece el orquestador.

## Pasos

```bash
curl -fsS 'http://localhost:8080/ws/licencias?wsdl' -o /tmp/licencias.wsdl
grep -E 'solicitarLicencia|idSolicitante|tipoLicencia|soap:address' /tmp/licencias.wsdl
```

Consulta el WSDL en el navegador o usa `less /tmp/licencias.wsdl` para inspeccionar el XML
completo. Localiza el nombre de la operación, los parámetros de entrada y la dirección del
servicio.

## Comprueba

- La operación publicada se llama `solicitarLicencia`.
- La solicitud necesita `idSolicitante` y `tipoLicencia`.
- El endpoint anunciado debe corresponder al servicio accesible en el puerto 8080.

El WSDL describe el contrato SOAP; no es una interfaz REST ni la lista de destinos internos.

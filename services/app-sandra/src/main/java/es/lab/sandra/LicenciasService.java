package es.lab.sandra;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Servicio SOAP de solicitud de licencias. Orquesta el flujo:
 * EVACSV (registro documental) → INSIDE (expediente) → ReportService (PDF).
 */
@Service
@WebService(serviceName = "LicenciasService", targetNamespace = "http://lab.es/sandra")
public class LicenciasService {

    private static final Logger log = LoggerFactory.getLogger(LicenciasService.class);

    private final DownstreamClient downstream;

    public LicenciasService(DownstreamClient downstream) {
        this.downstream = downstream;
    }

    @WebMethod(operationName = "solicitarLicencia")
    @WebResult(name = "resultado")
    public String solicitarLicencia(@WebParam(name = "idSolicitante") String idSolicitante,
                                    @WebParam(name = "tipoLicencia") String tipoLicencia) {
        log.info("Solicitud de licencia recibida: solicitante={} tipo={}", idSolicitante, tipoLicencia);
        simularLatencia();

        String idDocumento = downstream.registrarDocumento(idSolicitante);
        String idExpediente = downstream.crearExpediente(idDocumento);
        String urlPdf = downstream.generarInforme(idExpediente);

        String resultado = "LIC-2026-000123 APROBADA (documento=" + idDocumento
                + ", expediente=" + idExpediente + ", pdf=" + urlPdf + ")";
        log.info("Licencia resuelta: {}", resultado);
        return resultado;
    }

    private static void simularLatencia() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(50, 201));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

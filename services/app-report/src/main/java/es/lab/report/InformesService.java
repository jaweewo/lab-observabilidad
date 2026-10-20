package es.lab.report;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/** Servicio SOAP de generación de informes PDF (stub de ReportService). */
@Service
@WebService(serviceName = "InformesService", targetNamespace = "http://lab.es/report")
public class InformesService {

    private static final Logger log = LoggerFactory.getLogger(InformesService.class);

    @WebMethod(operationName = "generarInforme")
    @WebResult(name = "resultado")
    public String generarInforme(@WebParam(name = "idExpediente") String idExpediente) {
        log.info("generarInforme recibido: idExpediente={}", idExpediente);
        simularLatencia();
        String resultado = "http://app-report:8082/pdf/INF-2026-0001.pdf";
        log.info("generarInforme completado: {}", resultado);
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

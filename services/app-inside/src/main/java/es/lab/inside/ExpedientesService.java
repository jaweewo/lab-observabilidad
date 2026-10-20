package es.lab.inside;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/** Servicio SOAP de expediente electrónico (stub de INSIDE). */
@Service
@WebService(serviceName = "ExpedientesService", targetNamespace = "http://lab.es/inside")
public class ExpedientesService {

    private static final Logger log = LoggerFactory.getLogger(ExpedientesService.class);

    @WebMethod(operationName = "crearExpediente")
    @WebResult(name = "resultado")
    public String crearExpediente(@WebParam(name = "idDocumento") String idDocumento) {
        log.info("crearExpediente recibido: idDocumento={}", idDocumento);
        simularLatencia();
        String resultado = "EXP-INSIDE-2026-0042";
        log.info("crearExpediente completado: {}", resultado);
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

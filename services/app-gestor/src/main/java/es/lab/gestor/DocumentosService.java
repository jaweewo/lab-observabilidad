package es.lab.gestor;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/** Servicio SOAP de registro documental (stub del gestor documental). */
@Service
@WebService(serviceName = "DocumentosService", targetNamespace = "http://lab.es/gestor")
public class DocumentosService {

    private static final Logger log = LoggerFactory.getLogger(DocumentosService.class);

    @WebMethod(operationName = "registrarDocumento")
    @WebResult(name = "resultado")
    public String registrarDocumento(@WebParam(name = "idSolicitante") String idSolicitante) {
        log.info("registrarDocumento recibido: idSolicitante={}", idSolicitante);
        simularLatencia();
        String resultado = "DOC-GESTOR-2026-0001";
        log.info("registrarDocumento completado: {}", resultado);
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

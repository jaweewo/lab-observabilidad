package es.lab.orquestador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cliente de los servicios de backend. Envía sobres SOAP 1.1 con RestTemplate;
 * el agente de OpenTelemetry instrumenta la llamada HTTP y propaga {@code traceparent}.
 */
@Component
public class DownstreamClient {

    private static final Logger log = LoggerFactory.getLogger(DownstreamClient.class);
    private static final Pattern RESULTADO = Pattern.compile("<resultado>(.*?)</resultado>", Pattern.DOTALL);

    private final RestTemplate restTemplate;
    private final String gestorUrl;
    private final String reportUrl;
    private final String insideUrl;

    public DownstreamClient(RestTemplate restTemplate,
                            @Value("${downstream.gestor-url}") String gestorUrl,
                            @Value("${downstream.report-url}") String reportUrl,
                            @Value("${downstream.inside-url}") String insideUrl) {
        this.restTemplate = restTemplate;
        this.gestorUrl = gestorUrl;
        this.reportUrl = reportUrl;
        this.insideUrl = insideUrl;
    }

    public String registrarDocumento(String idSolicitante) {
        return invocar(gestorUrl + "/ws/documentos", "http://lab.es/gestor", "registrarDocumento",
                "<idSolicitante>" + idSolicitante + "</idSolicitante>");
    }

    public String crearExpediente(String idDocumento) {
        return invocar(insideUrl + "/ws/expedientes", "http://lab.es/inside", "crearExpediente",
                "<idDocumento>" + idDocumento + "</idDocumento>");
    }

    public String generarInforme(String idExpediente) {
        return invocar(reportUrl + "/ws/informes", "http://lab.es/report", "generarInforme",
                "<idExpediente>" + idExpediente + "</idExpediente>");
    }

    private String invocar(String url, String namespace, String operacion, String parametros) {
        String sobre = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns="%s">
                  <soapenv:Body>
                    <ns:%s>%s</ns:%s>
                  </soapenv:Body>
                </soapenv:Envelope>""".formatted(namespace, operacion, parametros, operacion);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.set("SOAPAction", "\"\"");

        log.info("Invocando {} en {}", operacion, url);
        String respuesta = restTemplate.postForObject(url, new HttpEntity<>(sobre, headers), String.class);

        Matcher m = RESULTADO.matcher(respuesta == null ? "" : respuesta);
        String resultado = m.find() ? m.group(1) : "desconocido";
        log.info("Respuesta de {}: {}", operacion, resultado);
        return resultado;
    }
}

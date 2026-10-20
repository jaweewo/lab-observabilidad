package es.lab.gestor;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GestorApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestorApplication.class, args);
    }

    /** Publica el servicio SOAP en /ws/documentos (WSDL: /ws/documentos?wsdl). */
    @Bean
    public Endpoint documentosEndpoint(Bus bus, DocumentosService service) {
        EndpointImpl endpoint = new EndpointImpl(bus, service);
        endpoint.publish("/documentos");
        return endpoint;
    }

    @Bean
    public FilterRegistrationBean<TraceResponseFilter> traceResponseFilter() {
        FilterRegistrationBean<TraceResponseFilter> registration = new FilterRegistrationBean<>(new TraceResponseFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }
}

package es.lab.evacsv;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EvacsvApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvacsvApplication.class, args);
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

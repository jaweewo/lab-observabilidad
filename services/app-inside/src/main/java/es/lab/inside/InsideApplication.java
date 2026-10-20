package es.lab.inside;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InsideApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsideApplication.class, args);
    }

    /** Publica el servicio SOAP en /ws/expedientes (WSDL: /ws/expedientes?wsdl). */
    @Bean
    public Endpoint expedientesEndpoint(Bus bus, ExpedientesService service) {
        EndpointImpl endpoint = new EndpointImpl(bus, service);
        endpoint.publish("/expedientes");
        return endpoint;
    }

    @Bean
    public FilterRegistrationBean<TraceResponseFilter> traceResponseFilter() {
        FilterRegistrationBean<TraceResponseFilter> registration = new FilterRegistrationBean<>(new TraceResponseFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }
}

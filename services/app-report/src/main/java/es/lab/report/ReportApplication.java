package es.lab.report;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportApplication.class, args);
    }

    /** Publica el servicio SOAP en /ws/informes (WSDL: /ws/informes?wsdl). */
    @Bean
    public Endpoint informesEndpoint(Bus bus, InformesService service) {
        EndpointImpl endpoint = new EndpointImpl(bus, service);
        endpoint.publish("/informes");
        return endpoint;
    }

    @Bean
    public FilterRegistrationBean<TraceResponseFilter> traceResponseFilter() {
        FilterRegistrationBean<TraceResponseFilter> registration = new FilterRegistrationBean<>(new TraceResponseFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }
}

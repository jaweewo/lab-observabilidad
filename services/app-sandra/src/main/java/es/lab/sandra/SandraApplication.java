package es.lab.sandra;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
public class SandraApplication {

    public static void main(String[] args) {
        SpringApplication.run(SandraApplication.class, args);
    }

    /** Publica el servicio SOAP en /ws/licencias (WSDL: /ws/licencias?wsdl). */
    @Bean
    public Endpoint licenciasEndpoint(Bus bus, LicenciasService service) {
        EndpointImpl endpoint = new EndpointImpl(bus, service);
        endpoint.publish("/licencias");
        return endpoint;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(2))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Bean
    public FilterRegistrationBean<TraceResponseFilter> traceResponseFilter() {
        FilterRegistrationBean<TraceResponseFilter> registration = new FilterRegistrationBean<>(new TraceResponseFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }
}

package es.lab.gestor;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Devuelve la cabecera W3C {@code traceparent} en la respuesta para que el cliente
 * conozca el Trace ID de su petición. El span actual lo crea el agente de OpenTelemetry.
 */
public class TraceResponseFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        SpanContext ctx = Span.current().getSpanContext();
        if (ctx.isValid()) {
            response.setHeader("traceparent",
                    "00-" + ctx.getTraceId() + "-" + ctx.getSpanId() + "-" + ctx.getTraceFlags().asHex());
        }
        chain.doFilter(request, response);
    }
}

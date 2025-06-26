package com.okstatelibrary.redbud.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.*;
import java.io.IOException;

/**
 * {@code RequestFilter} is a servlet filter that handles Cross-Origin Resource Sharing (CORS)
 * settings for incoming HTTP requests. It is annotated as a Spring {@link Component} and is
 * configured to execute with the highest precedence using {@link Order}.
 * 
 * <p>This filter adds appropriate CORS headers to HTTP responses to allow cross-origin requests
 * from a specified domain (e.g., Angular frontend on localhost:4200).</p>
 * 
 * <p>The filter also handles pre-flight {@code OPTIONS} requests by responding with the required
 * headers and an HTTP 200 OK status without passing the request further down the filter chain.</p>
 * 
 * <p>For all other request methods, the request proceeds through the normal filter chain.</p>
 * 
 * <p><strong>Note:</strong> This filter is primarily used during development. In production, CORS
 * should be handled with more secure and dynamic configurations.</p>
 * 
 * @author Damith Perera
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestFilter implements Filter {

    /**
     * Filters incoming HTTP requests to add CORS headers and handle pre-flight requests.
     *
     * @param req    the incoming {@link ServletRequest}, cast to {@link HttpServletRequest}
     * @param res    the outgoing {@link ServletResponse}, cast to {@link HttpServletResponse}
     * @param chain  the {@link FilterChain} to continue processing the request
     * @throws IOException      if an I/O error occurs during processing
     * @throws ServletException if the processing fails for some reason
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        // Set standard CORS headers
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // Handle pre-flight request
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            System.out.println("Pre-flight");

            response.setHeader("Access-Control-Allow-Methods", "POST,GET,DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers",
                    "authorization, content-type, access-control-request-headers, " +
                            "access-control-request-method, accept, origin, x-requested-with");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            try {
                chain.doFilter(req, res);
            } catch (Exception e) {
                e.printStackTrace(); // Consider logging this instead of printing stack trace in production
            }
        }
    }

    /**
     * Initializes the filter. Currently not used.
     *
     * @param filterConfig the {@link FilterConfig} object that contains config info
     */
    @Override
    public void init(FilterConfig filterConfig) {
        // No initialization needed
    }

    /**
     * Called when the filter is taken out of service. Currently not used.
     */
    @Override
    public void destroy() {
        // No resources to clean up
    }
}

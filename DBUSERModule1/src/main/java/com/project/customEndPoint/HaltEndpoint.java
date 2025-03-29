package com.project.customEndPoint;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Component
@Endpoint(id = "halt")
@WebFilter(urlPatterns = {"/actuator/halt"})
public class HaltEndpoint implements Filter, ApplicationListener<ApplicationReadyEvent> {

    private boolean webApplicationReadyToAcceptRequests = false;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        webApplicationReadyToAcceptRequests = true;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!webApplicationReadyToAcceptRequests) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            httpServletResponse.setHeader("Retry-After", "35");
            httpServletResponse.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service is unavailable.");
        } else filterChain.doFilter(servletRequest, servletResponse);
    }

    @ReadOperation
    public String requestHalt(@RequestParam long time) {
        webApplicationReadyToAcceptRequests = false;
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        webApplicationReadyToAcceptRequests = true;
        return "Request Resumed";
    }
}
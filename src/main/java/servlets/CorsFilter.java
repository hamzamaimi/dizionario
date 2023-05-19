package servlets;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static utils.ParametersLabels.FRONT_END_COMPLETE_DOMAIN;

@WebFilter(filterName = "CorsFilter")
public class CorsFilter implements Filter {
    private final List<String> allowedOrigins = Collections.singletonList(FRONT_END_COMPLETE_DOMAIN);

    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        // Access-Control-Allow-Origin
        String origin = req.getHeader("Origin");
        resp.setHeader("Access-Control-Allow-Origin", allowedOrigins.contains(origin) ? origin : "");
        resp.setHeader("Vary", "Origin");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        resp.setHeader("Access-Control-Max-Age", "3600");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");

        chain.doFilter(request, response);

    }
}

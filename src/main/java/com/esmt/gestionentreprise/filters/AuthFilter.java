package com.esmt.gestionentreprise.filters;

import com.esmt.gestionentreprise.service.JwtTokenService;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@WebFilter(urlPatterns = {"/dashboard", "/employees/*", "/departments/*", "/projects/*", "/tasks/*"})
public class AuthFilter implements Filter {

    @Inject
    private JwtTokenService jwtTokenService;
    private static final String ROLE_ADMIN = "Administrateur";
    private static final String ROLE_MANAGER = "Gestionnaire";
    private static final String ROLE_EMPLOYEE = "Employe";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        Cookie[] cookies = httpRequest.getCookies();
        String token = null;

        if (cookies != null) {
            Optional<Cookie> tokenCookie = Arrays.stream(cookies)
                    .filter(c -> "token".equals(c.getName()))
                    .findFirst();

            if (tokenCookie.isPresent()) {
                token = tokenCookie.get().getValue();
            }
        }

        if (token == null) {
            System.out.println("Token is missing");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth");
            return;
        }

        String extractedEmail = extractUserEmail(token);

        if (!jwtTokenService.isTokenValid(token, extractedEmail)) {
            System.out.println("Token is invalid or expired");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth");
            return;
        }

        String userRole = jwtTokenService.extractRole(token);

        if (!isUserAuthorizedForRequest(userRole, httpRequest.getRequestURI())) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/unauthorized");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isUserAuthorizedForRequest(String role, String requestURI) {
        String contextPath = "/gestion-entreprise-1.0-SNAPSHOT";
        String path = requestURI.substring(contextPath.length());

        if (ROLE_ADMIN.equals(role)) {
            return true;
        }

        if (ROLE_MANAGER.equals(role)) {
            return !path.startsWith("/employees");
        }

        if (ROLE_EMPLOYEE.equals(role)) {
            return path.startsWith("/tasks") || path.startsWith("/dashboard");
        }

        return false;
    }

    private String extractUserEmail(String token) {
        try {
            return jwtTokenService.extractEmail(token);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}

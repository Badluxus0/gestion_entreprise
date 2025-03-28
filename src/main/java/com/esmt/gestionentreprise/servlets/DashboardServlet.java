package com.esmt.gestionentreprise.servlets;

import com.esmt.gestionentreprise.service.*;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    @EJB
    private TaskService taskService;
    @EJB
    private ProjectService projectService;
    @EJB
    private EmployeeService employeeService;
    @EJB
    private DepartmentService departmentService;
    @EJB
    private JwtTokenService jwtTokenService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String token = null;

        if (cookies != null) {
            Optional<Cookie> tokenCookie = Arrays.stream(cookies)
                    .filter(c -> "token".equals(c.getName()))
                    .findFirst();

            if (tokenCookie.isPresent()) {
                token = tokenCookie.get().getValue();
            }
        }

        if (token == null || !jwtTokenService.isTokenValid(token, extractUserEmail(token))) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }

        long employeeCount = employeeService.getEmployeeCount();
        long departmentCount = departmentService.getDepartmentCount();
        long projectCount = projectService.getProjectCount();
        long taskCountInProgress = taskService.getTasksInProgressCount();

        // Préparation des données à afficher dans la vue
        request.setAttribute("employeeCount", employeeCount);
        request.setAttribute("departmentCount", departmentCount);
        request.setAttribute("projectCount", projectCount);
        request.setAttribute("taskCountInProgress", taskCountInProgress);

        String username = extractUsername(token);
        if (username != null) {
            request.setAttribute("userName", username);
        }

        String role = extractRole(token);
        if (role != null) {
            request.setAttribute("role", role);
        }

        // Envoi des données à la vue (JSP ou autre)
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }

    private String extractUserEmail(String token) {
        try {
            return jwtTokenService.extractEmail(token);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractUsername(String token) {
        try {
            return jwtTokenService.extractUsername(token);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractRole(String token) {
        try {
            return jwtTokenService.extractRole(token);
        } catch (Exception e) {
            return null;
        }
    }
}

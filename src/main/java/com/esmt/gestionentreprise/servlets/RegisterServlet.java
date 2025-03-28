package com.esmt.gestionentreprise.servlets;

import com.esmt.gestionentreprise.entities.User;
import com.esmt.gestionentreprise.service.AuthService;
import com.esmt.gestionentreprise.service.EmployeeService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {
    @EJB
    private AuthService authService;
    @EJB
    private EmployeeService employeeService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/login/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        if (employeeService.findByEmail(email) == null) {
            req.setAttribute("error", "L'email n'existe pas");
            req.getRequestDispatcher("/login/register.jsp").forward(req, resp);
            return;
        }
        
        if (!validPassword(password)) {
            req.setAttribute("error", "Le mot de passe n'est pas assez fort");
            req.getRequestDispatcher("/login/register.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(confirmPassword)) {
            req.setAttribute("error", "Les mots de passe ne correspondent pas");
            req.getRequestDispatcher("/login/register.jsp").forward(req, resp);
            return;
        }

        User registeredUser = authService.register(email, password);
        if (registeredUser == null) {
            req.setAttribute("error", "Échec de l'inscription. Veuillez réessayer.");
            req.getRequestDispatcher("/login/register.jsp").forward(req, resp);
            return;
        }

        req.getSession().setAttribute("user", registeredUser);
        resp.sendRedirect("http://localhost:8080/gestion-entreprise-1.0-SNAPSHOT/auth");
    }

    private boolean validPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        if (!password.matches(".*[0-9].*")) {
            return false;
        }
        return password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }
}

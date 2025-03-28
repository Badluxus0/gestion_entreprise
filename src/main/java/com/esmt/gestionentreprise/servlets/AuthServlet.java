package com.esmt.gestionentreprise.servlets;

import com.esmt.gestionentreprise.entities.User;
import com.esmt.gestionentreprise.service.AuthService;
import com.esmt.gestionentreprise.service.JwtTokenService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AuthServlet", urlPatterns = "/auth")
public class AuthServlet extends HttpServlet {
    @EJB
    private AuthService authService;
    @EJB
    private JwtTokenService jwtTokenService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/login/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        User user = authService.authenticate(email, password);

        if (user == null) {
            req.setAttribute("error", "Email ou mot de passe incorrect");
            req.getRequestDispatcher("/login/login.jsp").forward(req, resp);
            return;
        }

        String token = jwtTokenService.generateToken(user.getEmail(), user.getId());
        Cookie tokenCookie = new Cookie("token", token);
        tokenCookie.setMaxAge(3600); // 1 hour
        tokenCookie.setPath("/");
        tokenCookie.setHttpOnly(true);
        resp.addCookie(tokenCookie);

        req.getSession().setAttribute("username", user.getEmail().split("@")[0]);

        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }
}

package com.esmt.gestionentreprise.servlets;

import com.esmt.gestionentreprise.service.JwtTokenService;
import jakarta.ejb.EJB;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @EJB
    private JwtTokenService jwtTokenService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the token from cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> tokenCookie = Arrays.stream(cookies)
                    .filter(c -> "token".equals(c.getName()))
                    .findFirst();

            if (tokenCookie.isPresent()) {
                // Invalidate the cookie
                Cookie cookie = new Cookie("token", "");
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }

        // Invalidate session
        request.getSession().invalidate();

        // Redirect to login page
        response.sendRedirect(request.getContextPath() + "/auth");
    }
}

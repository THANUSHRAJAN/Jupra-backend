
package com.jupra.backend.config;

import com.jupra.backend.exception.ApiException;
import com.jupra.backend.service.JwtService;

import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Guards every "/api/admin/**" route (the founder dashboard's API).
 * Accepts the JWT either as "Authorization: Bearer <token>" or, for links that open in a
 * new browser tab (the document viewer), as a "?token=" query parameter.
 * All other routes are left untouched.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        // Allow CORS preflight requests to pass through without JWT validation.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();

        if (!path.startsWith("/api/admin")) {
            chain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);

        if (token == null || token.isBlank()) {
            unauthorized(response, "Please sign in to continue.");
            return;
        }

        try {

            Claims claims = jwtService.validate(token, "access");

            request.setAttribute("adminEmail", claims.getSubject());

            chain.doFilter(request, response);

        } catch (ApiException e) {

            unauthorized(response, e.getMessage());

        }
    }

    private String extractToken(HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        return request.getParameter("token");
    }

    private void unauthorized(
            HttpServletResponse response,
            String message) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.setContentType("application/json");

        response.getWriter().write(
                "{\"message\":\""
                        + message.replace("\"", "'")
                        + "\"}"
        );
    }
}


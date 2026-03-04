package com.menumgr.menumanagerprobackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final DbUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, DbUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String auth = request.getHeader("Authorization");
        System.out.println(">>> PATH: " + request.getRequestURI());
        System.out.println(">>> AUTH HEADER: " + auth);
        System.out.println(">>> METHOD: " + request.getMethod());

        if (auth == null || !auth.startsWith("Bearer ")) {
            System.out.println(">>> NO BEARER TOKEN");
            filterChain.doFilter(request, response);
            return;
        }

        String token = auth.substring(7);
        boolean valid = jwtService.isValid(token);
        System.out.println(">>> JWT VALID: " + valid);

        if (!valid) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtService.getSubject(token);
        System.out.println(">>> JWT SUBJECT: " + email);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println(">>> AUTH SET: " + authentication.getName() + " " + authentication.getAuthorities());
        }

        filterChain.doFilter(request, response);
    }
}
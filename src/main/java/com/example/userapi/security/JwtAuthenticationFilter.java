package com.example.userapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter that processes incoming requests and validates JWT tokens.
 * Extends OncePerRequestFilter to ensure single execution per request.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (shouldProcessToken(authorizationHeader)) {
            String jwtToken = extractTokenFromHeader(authorizationHeader);
            
            if (isValidToken(jwtToken)) {
                setSecurityContext(request, jwtToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean shouldProcessToken(String authorizationHeader) {
        return authorizationHeader != null && 
               authorizationHeader.startsWith(BEARER_PREFIX) &&
               SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private String extractTokenFromHeader(String authorizationHeader) {
        return authorizationHeader.substring(BEARER_PREFIX_LENGTH);
    }

    private boolean isValidToken(String token) {
        return jwtUtil.validateToken(token);
    }

    private void setSecurityContext(HttpServletRequest request, String token) {
        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);
        
        UsernamePasswordAuthenticationToken authenticationToken = 
            createAuthenticationToken(username, role);
        
        authenticationToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(String username, String role) {
        return new UsernamePasswordAuthenticationToken(
            username, 
            null, 
            Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}

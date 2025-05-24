package com.example.demo.config;

import com.example.demo.service.UserDetailsServiceImpl;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    private final JwtUtil jwtutil;
    private final UserDetailsServiceImpl userDetailsService;
    private static final Logger loggerAuthTokenFilter = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    public AuthTokenFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService){
        this.jwtutil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        try{
            String jwt = parseJwt(request);
            if(jwt != null && jwtutil.validateJwtToken(jwt)){
                String regNumber = jwtutil.getRegistrationNumberFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(regNumber);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }catch(Exception e){
            loggerAuthTokenFilter.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");
        if(headerAuth != null && headerAuth.startsWith("Bearer ")){
            return headerAuth.substring(7);
        }

        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.equals("/api/auth/login") || path.equals("/api/auth/register");
    }
}

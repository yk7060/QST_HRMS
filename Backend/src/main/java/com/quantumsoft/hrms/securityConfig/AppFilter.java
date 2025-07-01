package com.quantumsoft.hrms.securityConfig;

import com.quantumsoft.hrms.entity.User;
import com.quantumsoft.hrms.servicei.UserServicei;
import jakarta.annotation.PostConstruct;
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
public class AppFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    private Logger logger = LoggerFactory.getLogger(AppFilter.class);

//    @PostConstruct
//    public void init() {
//        logger.info("AppFilter initialized");
//    }

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        String path = request.getRequestURI();
//        logger.info("Checking filter skip for path: " + path);
//        return path.equals("/v3/api-docs") ||
//                path.startsWith("/v3/api-docs/") ||
//                path.equals("/swagger-ui.html") ||
//                path.startsWith("/swagger-ui") ||
//                path.startsWith("/swagger-resources") ||
//                path.startsWith("/webjars") ||
//                path.equals("/v3/api-docs.yaml");
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String bearerToken = header.substring(7);

            if (bearerToken.trim().isEmpty()) {
                logger.warn("Empty JWT token after Bearer prefix.");
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtService.extractUsername(bearerToken);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.validateToken(bearerToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}

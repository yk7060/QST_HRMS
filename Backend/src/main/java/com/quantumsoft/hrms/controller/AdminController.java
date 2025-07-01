package com.quantumsoft.hrms.controller;

import com.quantumsoft.hrms.dto.AdminLoginDto;
import com.quantumsoft.hrms.securityConfig.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

// @CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/admin")
public class AdminController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    /* Here AuthenticationManager will authenticate the request and if the request is authenticated then
       we are generating the jwt token */
 //   @PreAuthorize("hasAuthority('ADMIN') ")
    @PostMapping(value = "/login", consumes = "application/json", produces = "plain/text")
    public ResponseEntity<String> login(@RequestBody AdminLoginDto adminLoginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(adminLoginDto.getUsername(), adminLoginDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        if (authentication.isAuthenticated()) {

            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));

            if (!isAdmin) {
                return new ResponseEntity<>("Access Denied - You are not an Admin", HttpStatus.FORBIDDEN);
            }

            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse(null);

            String jwtToken = jwtService.generateToken(authentication.getName(), role);
            return ResponseEntity.ok(jwtToken);
        } else {
            return new ResponseEntity<>("User authentication failed..!", HttpStatus.UNAUTHORIZED);
        }
    }


}

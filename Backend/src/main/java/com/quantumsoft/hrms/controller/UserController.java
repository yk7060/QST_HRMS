package com.quantumsoft.hrms.controller;

import com.quantumsoft.hrms.dto.UpdateRoleInput;
import com.quantumsoft.hrms.dto.LoginDto;
import com.quantumsoft.hrms.entity.User;
import com.quantumsoft.hrms.enums.Action;
import com.quantumsoft.hrms.securityConfig.JwtService;
import com.quantumsoft.hrms.servicei.AuditLogServiceI;
import com.quantumsoft.hrms.servicei.UserServicei;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    @Autowired
    private UserServicei service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuditLogServiceI auditLogService;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/register", consumes = "application/json", produces = "plain/text")
    public ResponseEntity<String> register(@Valid @RequestBody User user){
        return new ResponseEntity<String>(service.register(user), HttpStatus.CREATED);
    }

    // This login functionality will be used for ADMIN/HR and User login.

    @PostMapping(value = "/login", consumes = "application/json", produces = "plain/text")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User authentication failed..!", HttpStatus.UNAUTHORIZED);
        }

        // Get role of authenticated user
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        // Prevent admin login via this endpoint
        if ("ROLE_ADMIN".equals(role)) {
            return new ResponseEntity<>("Admins must log in through the Admin portal.", HttpStatus.FORBIDDEN);
        }

        User user = service.findByUsername(loginDto.getUsername());

        if (user.isLoginFirstTime()) {
            return new ResponseEntity<>("You are trying to login first time, reset the password before login.", HttpStatus.NOT_ACCEPTABLE);
        } else if ("DEACTIVE".equalsIgnoreCase(user.getStatus().name())) {
            return new ResponseEntity<>("INACTIVE users are not allowed to login.", HttpStatus.NOT_ACCEPTABLE);
        }

        String jwtToken = jwtService.generateToken(authentication.getName(), role);
        auditLogService.logInfo(authentication.getName(), Action.LOGIN);
        return new ResponseEntity<>(jwtToken, HttpStatus.OK);
    }


    @GetMapping(value = "/forgotPwd")
    public ResponseEntity<String> forgotPassword(@RequestParam String email){
        return new ResponseEntity<String>(service.forgotPassword(email), HttpStatus.OK);
    }

    @PatchMapping(value = "/resetPwd")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword){
        return new ResponseEntity<String>(service.resetPassword(email, otp, newPassword), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    @PatchMapping(value = "/updateRole")
    public ResponseEntity<String> updateRole(@RequestBody UpdateRoleInput updateRoleInput){
        return new ResponseEntity<String>(service.updateRole(updateRoleInput), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/manageStatus/{userId}")
    public ResponseEntity<String> manageStatus(@PathVariable UUID userId, @RequestParam String status){
        return new ResponseEntity<String>(service.manageStatus(userId, status), HttpStatus.OK);
    }

    @GetMapping(value = "/single/{userId}")
    public ResponseEntity<User> getUser(@PathVariable UUID userId){
        return new ResponseEntity<User>(service.getUser(userId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER') or hasRole('FINANCE')")
    @GetMapping(value = "/users")
    public ResponseEntity<List<User>> getUsers(){
        return new ResponseEntity<List<User>>(service.getUsers(), HttpStatus.OK);
    }

    @GetMapping(value = "/logout")
    public ResponseEntity<String> logout(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info(authentication.getName());
        SecurityContextHolder.clearContext();
        auditLogService.logInfo(authentication.getName(), Action.LOGOUT);
        return new ResponseEntity<String>("User logout successfully...!", HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        List<User> usersByRole = service.getUsersByRole(role.toUpperCase());
        return new ResponseEntity<>(usersByRole, HttpStatus.OK);
    }

}

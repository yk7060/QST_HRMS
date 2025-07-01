package com.quantumsoft.hrms.securityConfig;

import com.quantumsoft.hrms.entity.Admin;
import com.quantumsoft.hrms.entity.User;
import com.quantumsoft.hrms.exception.ResourceNotFoundException;
import com.quantumsoft.hrms.repository.AdminRepository;
import com.quantumsoft.hrms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    /* Here we have already hard coded the admin credentials so that's why we have applied if condition
       to reduce response time.
       getAuthorities() method is used to extract user role from user record.
    */

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username.equals("admin")) {
            Optional<Admin> adminOptional = adminRepository.findByUsername(username);
            if (adminOptional.isPresent()) {
                return new org.springframework.security.core.userdetails.User(adminOptional.get().getUsername(), adminOptional.get().getPassword(), getAuthorities(adminOptional.get()));
            }
        }
        Optional<User> userOptional = userRepository.findByUsername(username);
        if(userOptional.isPresent())
            return new org.springframework.security.core.userdetails.User(userOptional.get().getUsername(), userOptional.get().getPassword(), getAuthorities(userOptional.get()));
        else
            throw new ResourceNotFoundException("Resource not found in the database");
    }

    public Collection<SimpleGrantedAuthority> getAuthorities(Admin admin){
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + admin.getRole()));
    }

    public Collection<SimpleGrantedAuthority> getAuthorities(User user){
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }
}

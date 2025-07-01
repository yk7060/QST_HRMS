package com.quantumsoft.hrms.serviceimpl;

import com.quantumsoft.hrms.entity.Admin;
import com.quantumsoft.hrms.enums.Role;
import com.quantumsoft.hrms.enums.Status;
import com.quantumsoft.hrms.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/* This class is used to automatically registered the Admin if it is not present in database.
   Here we are using CommandLineRunner functional interface which contains run() method.
   run() method get automatically executed when application starts running.
   That's why we are writing Admin creation logic inside this method */

@Service
public class AdminInitializer implements CommandLineRunner {

    @Value(value = "${admin.username}")
    private String username;

    @Value(value = "${admin.password}")
    private String password;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminRepository repository;

    private final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    @Override
    public void run(String... args) throws Exception {
        if(repository.findByUsername(username).isEmpty()){
            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(Role.ADMIN);
            admin.setStatus(Status.ACTIVE);

            repository.save(admin);
        }else
            logger.info("Admin is already registered...!");
    }
}

package com.quantumsoft.hrms.serviceimpl;

import com.quantumsoft.hrms.dto.UpdateRoleInput;
import com.quantumsoft.hrms.entity.User;
import com.quantumsoft.hrms.enums.Action;
import com.quantumsoft.hrms.enums.Status;
import com.quantumsoft.hrms.exception.ResourceNotFoundException;
import com.quantumsoft.hrms.repository.UserRepository;
import com.quantumsoft.hrms.servicei.AuditLogServiceI;
import com.quantumsoft.hrms.servicei.UserServicei;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserServicei {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value(value = "${spring.mail.username}")
    private String from;

    @Autowired
    private AuditLogServiceI auditLogService;

    @Override
    public String register(User user) {
        try {
            User savedUser = repository.save(user);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(savedUser.getEmail());
            message.setSubject("HRMS Credentials");
            message.setText("Your HRMS credentials are: \n\n" + "username: " + savedUser.getUsername() + "\n" + "password: " + savedUser.getPassword() + "\n" + "Please reset the password before login");
            javaMailSender.send(message);

            return "User saved successfully...!";
        }catch(Exception e){
            return "User not saved in database...!";
        }
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> userOptional = repository.findByUsername(username);
        if(userOptional.isPresent())
            return userOptional.get();
        else
            throw new ResourceNotFoundException("User record with given username not found in database.");
    }

    @Override
    public String forgotPassword(String email) {
       Optional<User> userOptional = repository.findByEmail(email);
       if(userOptional.isPresent()){

           String otp = String.valueOf(100000 + new Random().nextInt(90000));

           User user = userOptional.get();
           user.setOtp(passwordEncoder.encode(otp));
           user.setOtpGenerationTime(LocalDateTime.now());

           repository.save(user);

           SimpleMailMessage message = new SimpleMailMessage();
           message.setFrom(from);
           message.setTo(user.getEmail());
           message.setSubject("HRMS OTP");
           message.setText("Your HRMS OTP is: " + otp);
           javaMailSender.send(message);

           return "Otp has been sent to given email";
       }
        return "You have entered the wrong email address. Please enter the registered email address.";
    }

    @Override
    public String resetPassword(String email, String otp, String newPassword) {
        Optional<User> userOptional = repository.findByEmail(email);
        if(userOptional.isPresent()){
            System.out.println("user found");
            User user = userOptional.get();

            LocalDateTime otpExpiryTime = user.getOtpGenerationTime().plusMinutes(2);
            if(LocalDateTime.now().isAfter(otpExpiryTime)){
                return "OTP is expired";
            }
            else if(passwordEncoder.matches(otp, user.getOtp())){
                System.out.println("same otp");
                user.setOtp(null);
                user.setOtpGenerationTime(null);
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setLoginFirstTime(false);

                repository.save(user);

                auditLogService.logInfo(user.getUsername(), Action.PASSWORD_CHANGE);

                return "Password has been reset successfully...!";
            }else
                return "Invalid OTP";
        }else
            return "You have entered the wrong email address. Please enter the registered email address.";
    }

    @Override
    public List<User> getUsers() {
        return repository.findAll();
    }

    @Override
    public User getUser(UUID userId) {
        return repository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User record not found in database"));
    }

    @Override
    public String manageStatus(UUID userId, String status) {
        User user = repository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User record not found in database"));
        if(status.equals("INACTIVE")) {
            user.setStatus(Status.DEACTIVE);
        }else{
            user.setStatus(Status.ACTIVE);
        }
        repository.save(user);
        return "User status has been changed successfully...!";
    }

    @Override
    public String updateRole(UpdateRoleInput updateRoleInput) {
        User user = repository.findByEmail(updateRoleInput.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found in database"));
        user.setRole(updateRoleInput.getRole());
        repository.save(user);
        return "User role updated successfully...";
    }
    @Override
    public List<User> getUsersByRole(String role) {
        return repository.findByRole(role);
    }


}

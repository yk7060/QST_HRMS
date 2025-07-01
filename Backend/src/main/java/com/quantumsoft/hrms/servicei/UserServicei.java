package com.quantumsoft.hrms.servicei;

import com.quantumsoft.hrms.dto.UpdateRoleInput;
import com.quantumsoft.hrms.entity.User;
import org.springframework.http.HttpStatusCode;

import java.util.List;
import java.util.UUID;

public interface UserServicei {

    public String register(User user);

    public User findByUsername(String username);

    public String forgotPassword(String email);

    public String resetPassword(String email, String otp, String newPassword);

    public List<User> getUsers();

    public User getUser(UUID userId);

    public String manageStatus(UUID userId, String status);

    String updateRole(UpdateRoleInput updateRoleInput);
    List<User> getUsersByRole(String role);

}

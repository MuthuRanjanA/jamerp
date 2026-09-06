package com.tatastrive.erp.JAM.Enterprises.Controller;

import com.tatastrive.erp.JAM.Enterprises.Response.ApiResponse;
import com.tatastrive.erp.JAM.Enterprises.Role;
import com.tatastrive.erp.JAM.Enterprises.Service.UserService;
import com.tatastrive.erp.JAM.Enterprises.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${app.default-temp-password:TempPassword@123}")
    private String defaultTempPassword;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            List<UserDto> users = userService.getAllUsers();
            return ResponseEntity.ok(new ApiResponse("Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse> updateUserRole(@PathVariable Long id, @RequestParam Role role) {
        try {
            UserDto updated = userService.updateUserRole(id, role);
            return ResponseEntity.ok(new ApiResponse("User role updated to " + role, updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateUserStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        try {
            UserDto updated = userService.updateUserStatus(id, enabled);
            String statusStr = enabled ? "activated" : "deactivated";
            return ResponseEntity.ok(new ApiResponse("User account " + statusStr + " successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String newPassword = body.get("newPassword");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                newPassword = defaultTempPassword;
            }
            userService.resetPassword(id, newPassword);
            return ResponseEntity.ok(new ApiResponse("Password reset successfully. Temporary password: " + newPassword, newPassword));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }
}

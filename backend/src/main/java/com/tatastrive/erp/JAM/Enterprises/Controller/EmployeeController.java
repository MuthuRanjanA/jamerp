package com.tatastrive.erp.JAM.Enterprises.Controller;

import com.tatastrive.erp.JAM.Enterprises.Entity.Employee;

import com.tatastrive.erp.JAM.Enterprises.Response.ApiResponse;
import com.tatastrive.erp.JAM.Enterprises.Service.EmployeeService;
import com.tatastrive.erp.JAM.Enterprises.dto.CreateEmployeeRequest;
import com.tatastrive.erp.JAM.Enterprises.dto.CreateEmployeeResponse;
import com.tatastrive.erp.JAM.Enterprises.dto.EmployeeDTO;
import com.tatastrive.erp.JAM.Enterprises.dto.UpdateEmployeeRequest;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<ApiResponse> createEmployee(@RequestBody CreateEmployeeRequest request) {

        try {
            CreateEmployeeResponse response = employeeService.createEmployee(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse("Employee and login account created successfully", response)
                    );

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(exception.getMessage(), null));
        }
    }

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/team/{managerId}")
    public ResponseEntity<ApiResponse> getTeamMembers(@PathVariable Long managerId) {
        try {
            List<EmployeeDTO> team = employeeService.getTeamMembers(managerId);
            return ResponseEntity.ok(new ApiResponse("Team members retrieved successfully", team));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getEmployeeById(@PathVariable Long id) {
        try {
            EmployeeDTO employee = employeeService.getEmployeeById(id);

            return ResponseEntity.ok(
                    new ApiResponse("Employee Retrieved Successfully", employee)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getMyProfile(
            Authentication authentication) {

        try {
            String email = authentication.getName();

            EmployeeDTO employee = employeeService.getEmployeeByEmail(email);

            return ResponseEntity.ok(
                    new ApiResponse("Profile retrieved successfully", employee));

        } catch (Exception exception) {
            return ResponseEntity.status(
                    HttpStatus.NOT_FOUND
            ).body(
                    new ApiResponse(
                            exception.getMessage(),
                            null
                    )
            );
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateEmployee(
            @PathVariable Long id,
            @RequestBody UpdateEmployeeRequest request) {

        try {

            EmployeeDTO updatedEmployee =
                    employeeService.updateEmployee(id, request);

            return ResponseEntity.ok(
                    new ApiResponse(
                            "Employee Updated Successfully",
                            updatedEmployee
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(
                            new ApiResponse(
                                    e.getMessage(),
                                    null
                            )
                    );
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok(
                    new ApiResponse("Employee Deleted Successfully", null)
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

}

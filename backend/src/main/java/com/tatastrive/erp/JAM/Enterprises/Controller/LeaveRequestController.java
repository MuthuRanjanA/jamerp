package com.tatastrive.erp.JAM.Enterprises.Controller;

import com.tatastrive.erp.JAM.Enterprises.Entity.LeaveRequest;
import com.tatastrive.erp.JAM.Enterprises.Response.ApiResponse;
import com.tatastrive.erp.JAM.Enterprises.Service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/leave")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveService;

    // Apply leave
    @PostMapping
    public ResponseEntity<ApiResponse> applyLeave(@RequestBody LeaveRequest leaveRequest) {
        try {
            LeaveRequest saved = leaveService.applyLeave(leaveRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Leave request submitted successfully", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Approve leave
    @PutMapping("/approve/{id}")
    public ResponseEntity<ApiResponse> approveLeave(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> body) {
        try {
            Long approverId = body != null && body.containsKey("approverId") ? Long.parseLong(body.get("approverId").toString()) : null;
            String comment = body != null && body.containsKey("comment") ? body.get("comment").toString() : null;

            LeaveRequest approved = leaveService.approveLeave(id, approverId, comment);
            return ResponseEntity.ok(new ApiResponse("Leave request approved", approved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Reject leave
    @PutMapping("/reject/{id}")
    public ResponseEntity<ApiResponse> rejectLeave(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> body) {
        try {
            Long approverId = body != null && body.containsKey("approverId") ? Long.parseLong(body.get("approverId").toString()) : null;
            String comment = body != null && body.containsKey("comment") ? body.get("comment").toString() : null;

            LeaveRequest rejected = leaveService.rejectLeave(id, approverId, comment);
            return ResponseEntity.ok(new ApiResponse("Leave request rejected", rejected));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Get all leave requests
    @GetMapping
    public ResponseEntity<ApiResponse> getAllLeaveRequests() {
        try {
            List<LeaveRequest> list = leaveService.getAllLeaveRequests();
            return ResponseEntity.ok(new ApiResponse("Leave requests retrieved", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Get employee leaves
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse> getEmployeeLeaves(@PathVariable Long employeeId) {
        try {
            List<LeaveRequest> list = leaveService.getEmployeeLeaves(employeeId);
            return ResponseEntity.ok(new ApiResponse("Employee leave history retrieved", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Get manager team leaves
    @GetMapping("/team/{managerId}")
    public ResponseEntity<ApiResponse> getTeamLeaves(@PathVariable Long managerId) {
        try {
            List<LeaveRequest> list = leaveService.getTeamLeaves(managerId);
            return ResponseEntity.ok(new ApiResponse("Team leave requests retrieved", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }
}
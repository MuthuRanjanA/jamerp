package com.tatastrive.erp.JAM.Enterprises.Controller;

import com.tatastrive.erp.JAM.Enterprises.PayrollStatus;
import com.tatastrive.erp.JAM.Enterprises.Response.ApiResponse;
import com.tatastrive.erp.JAM.Enterprises.Service.PayRollService;
import com.tatastrive.erp.JAM.Enterprises.dto.PayRollDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payroll")
public class PayRollController {

    @Autowired
    private PayRollService payRollService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> generatePayroll(@RequestBody PayRollDto payrollDto) {
        try {
            PayRollDto created = payRollService.generatePayRoll(payrollDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Payroll generated successfully", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<PayRollDto>> getAllPayrolls() {
        return ResponseEntity.ok(payRollService.getAllPayRolls());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPayrollById(@PathVariable Long id) {
        try {
            PayRollDto dto = payRollService.getPayRollById(id);
            return ResponseEntity.ok(new ApiResponse("Payroll retrieved successfully", dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse> getPayrollsByEmployee(@PathVariable Long employeeId) {
        try {
            List<PayRollDto> payrolls = payRollService.getPayRollsByEmployee(employeeId);
            return ResponseEntity.ok(new ApiResponse("Employee payrolls retrieved successfully", payrolls));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/month/{month}/year/{year}")
    public ResponseEntity<ApiResponse> getPayrollsByMonthAndYear(
            @PathVariable String month,
            @PathVariable Integer year) {
        try {
            List<PayRollDto> payrolls = payRollService.getPayRollsByMonthAndYear(month, year);
            return ResponseEntity.ok(new ApiResponse("Monthly payrolls retrieved successfully", payrolls));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam PayrollStatus status) {
        try {
            PayRollDto updated = payRollService.updatePayRollStatus(id, status);
            return ResponseEntity.ok(new ApiResponse("Payroll status updated to " + status, updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deletePayroll(@PathVariable Long id) {
        try {
            payRollService.deletePayRoll(id);
            return ResponseEntity.ok(new ApiResponse("Payroll deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse> getSummary() {
        try {
            Map<String, Object> summary = payRollService.getPayrollSummary();
            return ResponseEntity.ok(new ApiResponse("Payroll summary retrieved successfully", summary));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }
}

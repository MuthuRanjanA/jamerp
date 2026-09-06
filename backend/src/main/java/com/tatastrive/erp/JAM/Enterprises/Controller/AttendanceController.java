package com.tatastrive.erp.JAM.Enterprises.Controller;

import com.tatastrive.erp.JAM.Enterprises.Entity.Attendance;
import com.tatastrive.erp.JAM.Enterprises.Response.ApiResponse;
import com.tatastrive.erp.JAM.Enterprises.Service.AttendanceService;
import com.tatastrive.erp.JAM.Enterprises.dto.AttendanceDashboardDto;
import com.tatastrive.erp.JAM.Enterprises.dto.AttendanceDto;
import com.tatastrive.erp.JAM.Enterprises.dto.EmployeeAttendanceStatusDto;
import com.tatastrive.erp.JAM.Enterprises.dto.ShiftAttendanceSummaryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping
    public AttendanceDto saveAttendance(@RequestBody Attendance attendance) {
        return attendanceService.saveAttendance(attendance);
    }

    @PostMapping("/check-in/{employeeId}")
    public ResponseEntity<ApiResponse> checkIn(
            @PathVariable Long employeeId,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            String location = body != null ? body.get("workLocation") : "OFFICE";
            String notes = body != null ? body.get("notes") : null;

            AttendanceDto dto = attendanceService.checkIn(employeeId, location, notes);
            return ResponseEntity.ok(new ApiResponse("Checked in successfully", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/check-out/{employeeId}")
    public ResponseEntity<ApiResponse> checkOut(@PathVariable Long employeeId) {
        try {
            AttendanceDto dto = attendanceService.checkOut(employeeId);
            return ResponseEntity.ok(new ApiResponse("Checked out successfully", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping
    public List<AttendanceDto> getAllAttendance() {
        return attendanceService.getAllAttendance();
    }

    @GetMapping("/employee/{employeeId}")
    public List<AttendanceDto> getAttendanceByEmployee(@PathVariable Long employeeId) {
        return attendanceService.getAttendanceByEmployee(employeeId);
    }

    @GetMapping("/team/{managerId}")
    public ResponseEntity<ApiResponse> getTeamAttendance(@PathVariable Long managerId) {
        try {
            List<AttendanceDto> teamAttendance = attendanceService.getTeamAttendance(managerId);
            return ResponseEntity.ok(new ApiResponse("Team attendance retrieved successfully", teamAttendance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse> getTodayAttendance() {
        try {
            List<AttendanceDto> list = attendanceService.getAttendanceByDate(LocalDate.now());
            return ResponseEntity.ok(new ApiResponse("Today's attendance retrieved successfully", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<AttendanceDto> list = attendanceService.getAttendanceByDate(date);
            return ResponseEntity.ok(new ApiResponse("Attendance for date retrieved successfully", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/month/{year}/{month}")
    public ResponseEntity<ApiResponse> getAttendanceByMonth(
            @PathVariable int year,
            @PathVariable int month) {
        try {
            List<AttendanceDto> list = attendanceService.getAttendanceByMonth(year, month);
            return ResponseEntity.ok(new ApiResponse("Monthly attendance retrieved successfully", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/employee/{employeeId}/month/{year}/{month}")
    public ResponseEntity<ApiResponse> getEmployeeAttendanceByMonth(
            @PathVariable Long employeeId,
            @PathVariable int year,
            @PathVariable int month) {
        try {
            List<AttendanceDto> list = attendanceService.getEmployeeAttendanceByMonth(employeeId, year, month);
            return ResponseEntity.ok(new ApiResponse("Employee monthly attendance retrieved successfully", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/team/{managerId}/date/{date}")
    public ResponseEntity<ApiResponse> getTeamAttendanceByDate(
            @PathVariable Long managerId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<AttendanceDto> list = attendanceService.getTeamAttendanceByDate(managerId, date);
            return ResponseEntity.ok(new ApiResponse("Team daily attendance retrieved successfully", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/team/{managerId}/month/{year}/{month}")
    public ResponseEntity<ApiResponse> getTeamAttendanceByMonth(
            @PathVariable Long managerId,
            @PathVariable int year,
            @PathVariable int month) {
        try {
            List<AttendanceDto> list = attendanceService.getTeamAttendanceByMonth(managerId, year, month);
            return ResponseEntity.ok(new ApiResponse("Team monthly attendance retrieved successfully", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getDashboardMetrics() {
        try {
            AttendanceDashboardDto metrics = attendanceService.getDashboardMetrics();
            return ResponseEntity.ok(new ApiResponse("Attendance dashboard metrics retrieved successfully", metrics));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse> getAttendanceStatus(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long managerId,
            @RequestParam(required = false) Long employeeId) {
        try {
            if (date == null) {
                date = LocalDate.now();
            }
            List<EmployeeAttendanceStatusDto> statusList = attendanceService.getAttendanceStatusForDate(date, managerId, employeeId);
            return ResponseEntity.ok(new ApiResponse("Attendance status retrieved successfully", statusList));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/shifts-summary")
    public ResponseEntity<ApiResponse> getShiftsSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long managerId) {
        try {
            if (date == null) {
                date = LocalDate.now();
            }
            List<ShiftAttendanceSummaryDto> summary = attendanceService.getShiftsSummaryForDate(date, managerId);
            return ResponseEntity.ok(new ApiResponse("Shifts summary retrieved successfully", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }
}

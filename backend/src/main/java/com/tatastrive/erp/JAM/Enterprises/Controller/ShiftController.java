package com.tatastrive.erp.JAM.Enterprises.Controller;

import com.tatastrive.erp.JAM.Enterprises.Entity.Shift;
import com.tatastrive.erp.JAM.Enterprises.Service.ShiftService;
import com.tatastrive.erp.JAM.Enterprises.dto.ShiftDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/shifts")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @PostMapping
    public ResponseEntity<Shift> createShift(@RequestBody ShiftDto shiftDto) {
        return ResponseEntity.ok(shiftService.createShift(shiftDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shift> updateShift(@PathVariable Long id, @RequestBody ShiftDto shiftDto) {
        return ResponseEntity.ok(shiftService.updateShift(id, shiftDto));
    }

    @GetMapping
    public ResponseEntity<List<Shift>> getAllShifts() {
        return ResponseEntity.ok(shiftService.getAllShifts());
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignShiftToEmployee(
            @RequestParam Long employeeId,
            @RequestParam Long shiftId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveTo) {
        shiftService.assignShiftToEmployee(employeeId, shiftId, effectiveFrom, effectiveTo);
        return ResponseEntity.ok("Shift assigned successfully");
    }
}

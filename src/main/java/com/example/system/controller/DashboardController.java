package com.example.system.controller;

import com.example.system.dto.HospitalDTO;
import com.example.system.dto.PharmacyDTO;
import com.example.system.dto.Search;
import com.example.system.service.DashboardService;
import com.example.system.service.HospitalService;
import com.example.system.service.PharmacyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final HospitalService hospitalService;
    private final PharmacyService pharmacyService;

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getGeneralStatistics() {
        return ResponseEntity.ok(dashboardService.getGeneralStatistics());
    }

    @GetMapping("/appointments/analysis")
    public ResponseEntity<Map<String, Map<String, Long>>> getAppointmentAnalysis() {
        return ResponseEntity.ok(dashboardService.getAppointmentAnalysis());
    }

    @GetMapping("/demographics")
    public ResponseEntity<Map<String, Map<String, Long>>> getDemographics() {
        return ResponseEntity.ok(dashboardService.getDemographics());
    }

    @GetMapping("/performance")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics() {
        return ResponseEntity.ok(dashboardService.getPerformanceMetrics());
    }

    @GetMapping("/trends")
    public ResponseEntity<Map<String, Object>> getTrendData() {
        return ResponseEntity.ok(dashboardService.getTrendData());
    }

    @GetMapping("/stats/departments")
    public ResponseEntity<Map<String, Integer>> getDepartments() {
        return ResponseEntity.ok(dashboardService.getAllDepartments());
    }

    @GetMapping("/nearby/hospital")
    public ResponseEntity<List<HospitalDTO>> getHospitalsNearby(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius) {
        List<HospitalDTO> hospitals = hospitalService.getHospitalsWithinRadius(latitude, longitude, radius);
        return ResponseEntity.ok(hospitals);
    }

    @GetMapping("/departments")
    public ResponseEntity<List<String>> getAllDepartmentNames() {
        return ResponseEntity.ok(hospitalService.getAllDepartments());
    }

    @GetMapping("/nearby/pharmacy")
    public ResponseEntity<List<PharmacyDTO>> getPharmaciesNearby(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius) {
        List<PharmacyDTO> pharmacies = pharmacyService.getPharmaciesWithinRadius(latitude, longitude, radius);
        return ResponseEntity.ok(pharmacies);
    }

    @GetMapping("/search")
    public ResponseEntity<Search> search(@RequestParam String keyword) {
        return ResponseEntity.ok(dashboardService.searchByKeyword(keyword));
    }

}

package com.example.system.controller;

import com.example.system.dto.*;
import com.example.system.entity.Doctor;
import com.example.system.exception.HospitalManagementException;
import com.example.system.service.DoctorService;
import com.example.system.service.PatientService;
import com.example.system.service.utils.Utility;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@AllArgsConstructor
public class DoctorController {

    private final Utility utility;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @GetMapping("/profile")
    public ResponseEntity<DoctorDTO> getDoctorProfile(@RequestHeader("Authorization") String token) {
        Doctor doctor = (Doctor) utility.getUserFromToken(token);
        return ResponseEntity.ok(doctorService.getDoctorProfile(doctor));
    }

    @Transactional
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteDoctor(@RequestHeader("Authorization") String token) {
        Doctor doctor = (Doctor) utility.getUserFromToken(token);
        doctorService.deleteProfile(doctor);
        return ResponseEntity.ok("Doctor deleted successfully.");
    }

    @GetMapping("/patients")
    public ResponseEntity<List<PatientDTO>> getDoctorPatients(@RequestHeader("Authorization") String token) {
        Doctor doctor = (Doctor) utility.getUserFromToken(token);
        return ResponseEntity.ok(patientService.getDoctorsPatient(doctor));
    }

    @PostMapping("/add-medical-history")
    public ResponseEntity<Response> addMedicalHistory(@RequestHeader("Authorization") String token,
                                                      @RequestBody HistoryRequest history) {
        Response response = new Response();
        try {
            Doctor doctor = (Doctor) utility.getUserFromToken(token);
            patientService.addMedicalHistory(doctor, history);
            response.setMessage("Medical History added successfully.");
            response.setStatus(HttpStatus.CREATED);
        } catch (HospitalManagementException e) {
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.setMessage("Error in adding Medical History: " + e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/add-lab-results")
    public ResponseEntity<Response> addLabResults(@ModelAttribute LabTestRequest labTest,
                                                  @RequestParam MultipartFile file) {
        Response response = new Response();
        try {
            patientService.addLabResults(file, labTest);
            response.setMessage("Medical History added successfully.");
            response.setStatus(HttpStatus.CREATED);
        } catch (HospitalManagementException e) {
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.setMessage("Error in adding Lab Results: " + e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}

package com.example.system.controller;

import com.example.system.dto.Password;
import com.example.system.dto.ProfileUpdateDTO;
import com.example.system.entity.*;
import com.example.system.entity.Pharmacist;
import com.example.system.entity.Doctor;
import com.example.system.entity.Manager;
import com.example.system.entity.Patient;
import com.example.system.exception.HospitalManagementException;
import com.example.system.service.*;
import com.example.system.service.utils.Utility;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UpdateController {

    private final Utility utility;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final HospitalService hospitalService;
    private final PharmacyService pharmacyService;

    @Transactional
    @PutMapping("/updateProfile")
    public ResponseEntity<String> updateProfile(@RequestHeader("Authorization") String token,
                                                @RequestParam ProfileUpdateDTO profileUpdateDTO, @RequestParam("image") MultipartFile image) {
        Object object = utility.getUserFromToken(token);
        if(object instanceof Doctor doctor){
            if(!doctor.getSchedules().isEmpty()) throw new HospitalManagementException("Profile is already up-to-date");
            doctorService.updateDoctor(doctor, profileUpdateDTO, image);
            return ResponseEntity.ok("Doctor profile updated successfully");
        }
        throw new HospitalManagementException("Something went wrong. Please try again later.");
    }

    @PutMapping("/add-medical")
    public ResponseEntity<String> addMedicalHistory(String token, @RequestBody List<MedicalHistory> medicalHistories){
        Object object = utility.getUserFromToken(token);
        if(object instanceof Patient patient){
            patientService.addMedicalHistory(patient, medicalHistories);
            return ResponseEntity.ok("Medical history added successfully");
        }
        throw new HospitalManagementException("Something went wrong. Please try again later.");
    }

    @PutMapping("/address")
    public ResponseEntity<String> updateAddress(@RequestHeader("Authorization") String token, @RequestBody Address address){
        Object object = utility.getUserFromToken(token);
        if(object instanceof Patient patient){
            patientService.updateAddress(patient, address);
        } else if (object instanceof Manager manager) {
            hospitalService.updateAddress(manager, address);
        } else if (object instanceof Pharmacist pharmacist) {
            pharmacyService.updateAddress(pharmacist, address);
        }
        return ResponseEntity.ok("Address updated successfully");
    }

    @PutMapping("/update/password")
    public ResponseEntity<String> updatePassword(@RequestHeader("Authorization") String token, @RequestBody Password password) {
        Object object = utility.getUserFromToken(token);
        if(object instanceof Patient patient){
            patientService.updatePassword(patient, password);
        } else if (object instanceof Manager manager) {
            hospitalService.updatePassword(manager, password);
        } else if (object instanceof Pharmacist pharmacist) {
            pharmacyService.updatePassword(pharmacist, password);
        } else if (object instanceof Doctor doctor) {
            doctorService.updatePassword(doctor, password);
        }
        return ResponseEntity.ok("Password updated successfully");
    }

}

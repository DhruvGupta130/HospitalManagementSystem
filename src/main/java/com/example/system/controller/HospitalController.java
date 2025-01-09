package com.example.system.controller;

import com.example.system.dto.*;
import com.example.system.entity.Hospital;
import com.example.system.entity.Manager;
import com.example.system.exception.HospitalManagementException;
import com.example.system.repository.UserRepo;
import com.example.system.service.DoctorService;
import com.example.system.service.HospitalService;
import com.example.system.service.utils.Utility;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/hospital")
public class HospitalController {

    private final HospitalService hospitalService;
    private final Utility utility;
    private final DoctorService doctorService;
    private final UserRepo userRepo;

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<Response> registerHospital(@RequestHeader("Authorization") String token,
                                                     @RequestBody Hospital hospital) {
        Response response = new Response();
        try {
            Manager manager = (Manager) utility.getUserFromToken(token);
            hospitalService.registerHospital(hospital, manager);
            response.setMessage("Hospital registered successfully");
            response.setStatus(HttpStatus.CREATED);
        } catch (HospitalManagementException e) {
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.setMessage("Error registering hospital: " + e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/manager")
    public ResponseEntity<ManagerDTO> getHospitalManager(@RequestHeader("Authorization") String token) {
        Manager manager = (Manager) utility.getUserFromToken(token);
        return ResponseEntity.ok(hospitalService.getManagerProfile(manager));
    }
    @GetMapping
    public ResponseEntity<HospitalDTO> getHospital(@RequestHeader("Authorization") String token) {
        Manager manager = (Manager) utility.getUserFromToken(token);
        if(manager.getHospital() == null) throw new HospitalManagementException("Hospital not registered!");
        return ResponseEntity.ok(hospitalService.getHospitalProfile(manager.getHospital()));
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorDTO>> getAllDoctors(@RequestHeader("Authorization") String token) {
        Manager manager = (Manager) utility.getUserFromToken(token);
        if (manager.getHospital() == null) throw new HospitalManagementException("Please register hospital first!");
        return ResponseEntity.ok(hospitalService.getAllDoctors(manager.getHospital()));
    }

    @PostMapping("/doctors/register")
    public ResponseEntity<Response> registerDoctor(@RequestHeader("Authorization") String token, @RequestBody RegistrationDTO registrationDTO) {
        Response response = new Response();
        try {
            userRepo.findByUsername(registrationDTO.getUsername()).ifPresent(_ -> {
                throw new HospitalManagementException("User already exists");
            });
            Manager manager = (Manager) utility.getUserFromToken(token);
            if (manager.getHospital() == null) throw new HospitalManagementException("Please register hospital first!");
            registrationDTO.setHospitalId(manager.getHospital().getId());
            registrationDTO.setRole(UserRole.ROLE_DOCTOR);
            doctorService.registerDoctor(registrationDTO);
            response.setMessage("Doctor registered successfully");
            response.setStatus(HttpStatus.CREATED);
        } catch (HospitalManagementException e) {
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.setMessage("Error registering doctor" + e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/doctors/upload")
    public ResponseEntity<Response> addDoctors(@RequestHeader("Authorization") String token,
                                               @RequestParam("file") MultipartFile file) {
        Response response = new Response();
        try {
            Manager manager = (Manager) utility.getUserFromToken(token);
            if (manager.getHospital() == null) throw new HospitalManagementException("Please register hospital first!");
            doctorService.saveFromExcel(file, manager.getHospital().getId());
            response.setMessage("Doctors added successfully");
            response.setStatus(HttpStatus.CREATED);
        } catch (HospitalManagementException e) {
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.setMessage("Error adding doctor" + e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}

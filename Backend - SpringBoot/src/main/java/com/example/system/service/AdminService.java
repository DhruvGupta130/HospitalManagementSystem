package com.example.system.service;

import com.example.system.dto.AdminDTO;
import com.example.system.dto.AppointmentDTO;
import com.example.system.dto.DoctorDTO;
import com.example.system.dto.PatientDTO;
import com.example.system.entity.Admin;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdminService {

    AdminDTO getProfile(Admin admin);
    List<DoctorDTO> getAllDoctors();
    List<PatientDTO> getAllPatients();
    List<AppointmentDTO> getAllAppointments();
}

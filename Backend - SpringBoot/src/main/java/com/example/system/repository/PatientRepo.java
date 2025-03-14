package com.example.system.repository;

import com.example.system.dto.HospitalPatientDTO;
import com.example.system.dto.PharmacyPatientsDTO;
import com.example.system.entity.Doctor;
import com.example.system.entity.Hospital;
import com.example.system.entity.Patient;
import com.example.system.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepo extends JpaRepository<Patient, Long> {

    @Query("select p from Patient p join p.loginUser u where u.username = :username")
    Optional<Patient> findByUsername(String username);

    @Query("select p from Patient p join p.loginUser u where u.email = :email")
    Optional<Patient> findByEmail(String email);

    @Query("SELECT p from Patient p JOIN p.appointments a WHERE a.doctor = :doctor")
    List<Patient> findPatientsByDoctor(Doctor doctor);

    @Query("SELECT new com.example.system.dto.HospitalPatientDTO(p.id, " +
            "CONCAT(p.firstName, ' ', p.lastName), u.email, p.mobile, p.dateOfBirth, " +
            "p.gender, d.id, CONCAT(d.firstName, ' ', d.lastName), d.department) " +
            "FROM Patient p " +
            "JOIN p.loginUser u " +
            "JOIN p.medicalHistories m " +
            "JOIN m.doctor d " +
            "WHERE d.hospital = :hospital")
    List<HospitalPatientDTO> findPatientsByHospital(Hospital hospital);

    @Query("SELECT new com.example.system.dto.PharmacyPatientsDTO(p.id, " +
            "CONCAT(p.firstName, ' ', p.lastName), u.email, p.mobile, p.dateOfBirth, " +
            "p.gender, m.id, m.medicationName, m.quantity, m.price) " +
            "FROM Patient p " +
            "JOIN p.loginUser u " +
            "JOIN p.medications m " +
            "WHERE m.pharmacy = :pharmacy")
    List<PharmacyPatientsDTO> findPatientByPharmacy(Pharmacy pharmacy);



}


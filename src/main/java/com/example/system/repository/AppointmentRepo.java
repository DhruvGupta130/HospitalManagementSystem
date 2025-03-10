package com.example.system.repository;

import com.example.system.dto.AppointmentStatus;
import com.example.system.entity.Appointment;
import com.example.system.entity.Doctor;
import com.example.system.entity.Patient;
import jakarta.validation.constraints.FutureOrPresent;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepo extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

    List<Appointment> findAllByDoctor(Doctor doctor, Sort sort);
    List<Appointment> findAllByPatient(Patient patient, Sort sort);

    List<Appointment> findAppointmentsByAppointmentDateBetweenAndStatus(@FutureOrPresent(message = "Appointment date must not be in the past") LocalDateTime appointmentDateAfter, @FutureOrPresent(message = "Appointment date must not be in the past") LocalDateTime appointmentDateBefore, AppointmentStatus status);

    long countByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);

    @Modifying
    @Query("UPDATE Appointment a SET a.status = 'EXPIRED' WHERE a.appointmentDate < :now AND a.status NOT IN ('COMPLETED', 'CANCELLED')")
    void markExpiredAppointments(LocalDateTime now);

}

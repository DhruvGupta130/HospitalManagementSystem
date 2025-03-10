package com.example.system.entity;

import com.example.system.dto.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Patient patient;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Doctor doctor;

    @FutureOrPresent(message = "Appointment date must not be in the past")
    private LocalDateTime appointmentDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime lastUpdatedAt;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Column(length = 500)
    private String cancellationReason;

    private String createdBy;
    private String lastModifiedBy;
    private int slotIndex;

}

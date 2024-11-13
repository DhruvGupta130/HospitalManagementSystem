package com.example.system.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String medicationName;
    @NotNull
    private String compositionName;
    @NotNull
    private String dosageForm;
    @NotNull
    private String strength;
    @PositiveOrZero
    private long quantity;

    @NotNull
    @Future
    private LocalDate expiry;

    @NotNull
    private String manufacturer;
    @Positive
    private double price;
    @NotNull
    private String batchNumber;

    @NotNull
    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Pharmacy pharmacy;
}

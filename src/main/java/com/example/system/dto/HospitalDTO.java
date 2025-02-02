package com.example.system.dto;

import com.example.system.entity.Address;
import com.example.system.entity.Feedback;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class HospitalDTO {

    private long id;
    private String hospitalName;
    private Address address;
    private String email;
    private String mobile;
    private Set<String> departments;
    private String website;
    private int establishedYear;
    private String overview;
    private String specialities;
    private boolean emergencyServices;
    private int bedCapacity;
    private int icuCapacity;
    private int operationTheaters;
    private String technology;
    private String accreditations;
    private String insurancePartners;
    private List<Feedback> feedbacks;
    private List<String> images;

}

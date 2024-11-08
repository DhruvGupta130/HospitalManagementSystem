package com.example.system.service;

import com.example.system.dto.HospitalDTO;
import com.example.system.entity.Hospital;
import com.example.system.entity.HospitalManager;
import org.springframework.stereotype.Service;

@Service
public interface HospitalService {

    void registerHospital(HospitalDTO hospitalDTO, HospitalManager manager);
    Hospital getHospitalProfile(HospitalManager manager);
    HospitalManager getHospitalManagerProfile(HospitalManager manager);
}

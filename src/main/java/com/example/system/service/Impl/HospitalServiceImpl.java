package com.example.system.service.Impl;

import com.example.system.dto.HospitalDTO;
import com.example.system.entity.Doctor;
import com.example.system.entity.Hospital;
import com.example.system.entity.HospitalManager;
import com.example.system.repository.AddressRepo;
import com.example.system.repository.DoctorRepo;
import com.example.system.repository.HospitalRepo;
import com.example.system.repository.ManagerRepo;
import com.example.system.service.HospitalService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class HospitalServiceImpl implements HospitalService {

    private final DoctorRepo doctorRepo;
    private final HospitalRepo hospitalRepo;
    private final ManagerRepo managerRepo;
    private final AddressRepo addressRepo;

    @Override
    @Transactional
    public void registerHospital(HospitalDTO hospitalDTO, HospitalManager manager) {
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalDTO, hospital);
        addressRepo.save(hospital.getAddress());
        hospital.setManager(manager);
        hospitalRepo.save(hospital);
        manager.setHospital(hospital);
        managerRepo.save(manager);
    }

    @Override
    public Hospital getHospitalProfile(HospitalManager manager) {
        manager.getHospital().getDepartments().forEach(System.out::println);
        return manager.getHospital();
    }

    @Override
    public HospitalManager getHospitalManagerProfile(HospitalManager manager) {
        return manager;
    }
}

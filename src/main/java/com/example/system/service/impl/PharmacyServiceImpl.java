package com.example.system.service.impl;

import com.example.system.dto.MedicationDTO;
import com.example.system.dto.Password;
import com.example.system.dto.PharmacistDTO;
import com.example.system.dto.PharmacyDTO;
import com.example.system.entity.Address;
import com.example.system.entity.Medication;
import com.example.system.entity.Pharmacist;
import com.example.system.entity.Pharmacy;
import com.example.system.exception.HospitalManagementException;
import com.example.system.repository.AddressRepo;
import com.example.system.repository.MedicationRepo;
import com.example.system.repository.PharmacistRepo;
import com.example.system.repository.PharmacyRepo;
import com.example.system.service.PharmacyService;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PharmacyServiceImpl implements PharmacyService {

    private final PharmacyRepo pharmacyRepo;
    private final PharmacistRepo pharmacistRepo;
    private final AddressRepo addressRepo;
    private final MedicationRepo medicationRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void createPharmacy(Pharmacy pharmacy, Pharmacist pharmacist) {
        try {
            pharmacy.setPharmacist(pharmacist);
            pharmacist.setPharmacy(pharmacy);
            pharmacyRepo.save(pharmacy);
        } catch (Exception e) {
            throw new HospitalManagementException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updatePharmacy(PharmacyDTO pharmacyDTO, Pharmacist pharmacist) {
        Pharmacy pharmacy = pharmacyRepo.findById(pharmacist.getPharmacy().getId())
                .orElseThrow(() -> new HospitalManagementException("Pharmacy not found"));
        if(pharmacyDTO.getPharmacyName()!=null) pharmacy.setPharmacyName(pharmacyDTO.getPharmacyName());
        if(pharmacyDTO.getEmail()!=null) pharmacy.setEmail(pharmacyDTO.getEmail());
        if(pharmacyDTO.getMobile()!=null) pharmacy.setMobile(pharmacyDTO.getMobile());
        if(pharmacyDTO.getClosingTime()!=null) pharmacy.setClosingTime(pharmacyDTO.getClosingTime());
        if(pharmacyDTO.getOpeningTime()!=null) pharmacy.setOpeningTime(pharmacyDTO.getOpeningTime());
        if(pharmacyDTO.getImages()!=null) pharmacy.setImages(pharmacyDTO.getImages());
        pharmacy.setOpen(true);
        pharmacyRepo.save(pharmacy);
    }

    @Override
    public PharmacyDTO getPharmacyProfile(Pharmacy pharmacy) {
        if(pharmacy == null) throw new HospitalManagementException("Pharmacy not found");
        return new PharmacyDTO(
                pharmacy.getId(), pharmacy.getPharmacyName(),
                pharmacy.getOverview(), pharmacy.getServices(),
                pharmacy.getPharmacyTechnology(), pharmacy.getAccreditations(),
                pharmacy.getInsurancePartners(),
                pharmacy.getAddress(), pharmacy.getEmail(),
                pharmacy.getMobile(), pharmacy.getWebsite(),
                pharmacy.isOpen(), pharmacy.getOpeningTime(),
                pharmacy.getClosingTime(), pharmacy.getImages()
        );
    }

    @Override
    public PharmacistDTO getPharmacistProfile(Pharmacist pharmacist) {
        return new PharmacistDTO(
                pharmacist.getId(), pharmacist.getFirstName(),
                pharmacist.getLastName(), pharmacist.getGender(),
                pharmacist.getEmail(), pharmacist.getMobile(),
                pharmacist.getFirstName() + " " + pharmacist.getLastName()
        );
    }

    @Override
    public void updatePharmacy(Medication medication, Pharmacist pharmacist) {
        if(pharmacist.getPharmacy()==null) throw new HospitalManagementException("Pharmacy not found");
        medication.setPharmacy(pharmacist.getPharmacy());
        medicationRepo.save(medication);
    }

    @Override
    @Transactional
    public void saveFromExcel(MultipartFile file, Pharmacist pharmacist) throws HospitalManagementException {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next();
            while (rows.hasNext()) {
                Row row = rows.next();
                LocalDate expiryDate = row.getCell(5).getLocalDateTimeCellValue().toLocalDate();
                if(expiryDate.isBefore(LocalDate.now())) continue;
                String batchNumber = row.getCell(8).getStringCellValue().trim();
                Optional<Medication> existingMedication = medicationRepo.findByBatchNumber(batchNumber);
                if (existingMedication.isPresent()) {
                    updateMedication(existingMedication.get(), (int) row.getCell(4).getNumericCellValue());
                } else {
                    Medication medication = createMedication(row);
                    updatePharmacy(medication, pharmacist);
                }
            }
        } catch (IOException e) {
            throw new HospitalManagementException("Error reading the Excel file: " + e.getMessage(), e);
        }
    }
    private Medication createMedication(Row row) {
        try {
            Medication medication = new Medication();
            medication.setMedicationName(row.getCell(0).getStringCellValue());
            medication.setCompositionName(row.getCell(1).getStringCellValue());
            medication.setDosageForm(row.getCell(2).getStringCellValue());
            medication.setStrength(row.getCell(3).getStringCellValue());
            medication.setQuantity((int) row.getCell(4).getNumericCellValue());
            medication.setExpiry(row.getCell(5).getLocalDateTimeCellValue().toLocalDate());
            medication.setManufacturer(row.getCell(6).getStringCellValue());
            medication.setPrice(row.getCell(7).getNumericCellValue());
            medication.setBatchNumber(row.getCell(8).getStringCellValue());
            return medication;
        } catch (Exception e) {
            throw new HospitalManagementException("Error creating Medication: " + e.getMessage(), e);
        }
    }
    private void updateMedication(Medication medication, int quantity) {
        try {
            medication.setQuantity(quantity);
            medicationRepo.save(medication);
        } catch (Exception e) {
            throw new HospitalManagementException("Error updating medication: " + e.getMessage(), e);
        }
    }

    @Override
    public void setStatus(Pharmacy pharmacy) {
        if(pharmacy.getClosingTime().isBefore(LocalTime.now())
                || pharmacy.getOpeningTime().isAfter(LocalTime.now())) {
            pharmacy.setOpen(false);
        }else pharmacy.setOpen(!pharmacy.isOpen());
        pharmacyRepo.save(pharmacy);
    }

    @Override
    public void updatePassword(Pharmacist pharmacist, Password password) {
        if(!passwordEncoder.matches(password.getOldPassword(), pharmacist.getLoginUser().getPassword()))
            throw new HospitalManagementException("Provided password is incorrect");
        pharmacist.getLoginUser().setPassword(passwordEncoder.encode(password.getNewPassword()));
        pharmacistRepo.save(pharmacist);
    }

    @Override
    public void updateAddress(Pharmacist pharmacist, Address address) {
        Address address1 = addressRepo.findById(pharmacist.getPharmacy().getAddress().getId())
                .orElseThrow(() -> new HospitalManagementException("Address not found"));
        address.setId(address1.getId());
        addressRepo.save(address);
    }

    @Scheduled(fixedRate = 1800000)
    public void updatePharmacyStatus() {
        List<Pharmacy> pharmacies = pharmacyRepo.findAll();
        for (Pharmacy pharmacy : pharmacies) {
            this.updateStatus(pharmacy);
        }
    }

    private void updateStatus(Pharmacy pharmacy) {
        if(pharmacy.getClosingTime().isBefore(LocalTime.now())
                || pharmacy.getOpeningTime().isAfter(LocalTime.now())) {
            pharmacy.setOpen(false);
            pharmacyRepo.save(pharmacy);
        }
    }

    @Override
    public List<MedicationDTO> getMedications() {
        return medicationRepo.findAll().stream().map(this::getMedicationDTO).toList();
    }

    @Override
    public List<PharmacyDTO> getPharmaciesByKeyword(String keyword) {
        return pharmacyRepo.findPharmaciesByKeyword(keyword).stream()
                .map(this::getPharmacyProfile).toList();
    }

    @Override
    public List<PharmacyDTO> getPharmaciesWithinRadius(double latitude, double longitude, double radius) {
        List<Pharmacy> pharmacies = medicationRepo.findPharmacyWithinRadius(latitude, longitude, radius);
        return pharmacies.stream().map(this::getPharmacyProfile).toList();
    }

    @Override
    public List<MedicationDTO> getMedicationsByKeyword(String keyword) {
        return medicationRepo.findByKeyword(keyword).stream().map(this::getMedicationDTO).toList();
    }

    private MedicationDTO getMedicationDTO(Medication medication) {
        return new MedicationDTO(
                medication.getId(), medication.getMedicationName(),
                medication.getCompositionName(), medication.getDosageForm(),
                medication.getStrength(), medication.getQuantity(),
                medication.getExpiry(), medication.getExpiry().isBefore(LocalDate.now()),
                medication.getManufacturer(), medication.getPrice(),
                medication.getBatchNumber()
        );
    }
}
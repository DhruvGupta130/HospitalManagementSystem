package com.example.system.service.impl;

import com.example.system.dto.RecordsDTO;
import com.example.system.entity.Patient;
import com.example.system.entity.PatientRecord;
import com.example.system.repository.PatientRecordRepo;
import com.example.system.service.RecordService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class RecordServiceImpl implements RecordService {

    private final PatientRecordRepo patientRecordRepo;

    @Value("${file.storage.records.path}")
    private String storagePath;

    public RecordServiceImpl(PatientRecordRepo patientRecordRepo) {
        this.patientRecordRepo = patientRecordRepo;
    }

    @Override
    @Transactional
    public void uploadFile(Patient patient, MultipartFile file, String description) throws IOException {
        String patientDirPath = storagePath + "/" + patient.getId();
        String filePath = patientDirPath + "/" + file.getOriginalFilename();
        Path parentDir = Paths.get(patientDirPath);
        Path dest = Paths.get(filePath);
        if(Files.notExists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        file.transferTo(dest);
        PatientRecord record = new PatientRecord();
        record.setPatient(patient);
        record.setFileName(file.getOriginalFilename());
        record.setFilePath(filePath);
        record.setFileType(file.getContentType());
        record.setDescription(description);
        patientRecordRepo.save(record);
    }

    @Override
    public RecordsDTO getPatientRecords(PatientRecord patientRecord) {
        return new RecordsDTO(
                patientRecord.getId(), patientRecord.getPatient(),
                patientRecord.getFileName(), patientRecord.getFilePath(),
                patientRecord.getUploadDate(), patientRecord.getFileType(),
                patientRecord.getDescription()
        );
    }

    @Override
    public Resource downloadFile(Long fileId) throws IOException {
        PatientRecord record = patientRecordRepo.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found"));
        return new FileSystemResource(Path.of(record.getFilePath()));
    }

    @Override
    @Transactional
    public void deleteFile(Long fileId) throws IOException {
        PatientRecord record = patientRecordRepo.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found"));
        Path filePath = Paths.get(record.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        patientRecordRepo.delete(record);
    }

    @Override
    public List<RecordsDTO> getFilesByPatientId(Patient patient) {
        return patientRecordRepo.findByPatient(patient)
                .stream().map(this::getPatientRecords).toList();
    }
}

package com.example.system.service;

import com.example.system.entity.Patient;
import com.example.system.entity.PatientRecord;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface PatientRecordService {

    PatientRecord uploadFile(Patient patient, MultipartFile file, String description) throws IOException;
    Resource downloadFile(Long fileId) throws IOException;
    void deleteFile(Long fileId) throws IOException;
    List<PatientRecord> getFilesByPatientId(Patient patient);
}
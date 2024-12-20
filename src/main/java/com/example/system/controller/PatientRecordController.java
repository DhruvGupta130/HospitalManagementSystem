package com.example.system.controller;

import com.example.system.entity.Patient;
import com.example.system.entity.PatientRecord;
import com.example.system.exception.HospitalManagementException;
import com.example.system.service.PatientRecordService;
import com.example.system.service.utils.Utility;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/patient")
public class PatientRecordController {

    private final PatientRecordService patientRecordService;
    private final Utility utility;

    @PostMapping("/upload")
    public ResponseEntity<PatientRecord> uploadFile(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description) throws IOException {

        Patient patient = (Patient) utility.getUserFromToken(token);
        PatientRecord record = patientRecordService.uploadFile(patient, file, description);
        return ResponseEntity.ok(record);
    }

    @GetMapping("/files")
    public ResponseEntity<List<PatientRecord>> getFilesByPatient(@RequestHeader("Authorization") String token) {
        Patient patient = (Patient) utility.getUserFromToken(token);
        List<PatientRecord> records = patientRecordService.getFilesByPatientId(patient);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        Resource file;
        try {
            file = patientRecordService.downloadFile(fileId);
        } catch (IOException e) {
            throw new HospitalManagementException(e.getMessage());
        }

        String contentType = "application/octet-stream";
        try {
            if(file.getFilename()!=null)
                contentType = Files.probeContentType(Paths.get(file.getFilename()));
        } catch (IOException e) {
            throw new HospitalManagementException(e.getMessage());
        }

        if (contentType.startsWith("image/") || contentType.equals("application/pdf")) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(file);
        }

        String contentDisposition = "attachment; filename=\"" + file.getFilename() + "\"";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(file);
    }

    @Transactional
    @DeleteMapping("/{fileId}/delete")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId) {
        try {
            patientRecordService.deleteFile(fileId);
        } catch (IOException e) {
            throw new HospitalManagementException(e.getMessage());
        }
        return ResponseEntity.ok("File deleted successfully!");
    }
}


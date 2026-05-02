package com.hospital.service.impl;

import com.hospital.dto.request.PatientProfileRequest;
import com.hospital.entity.Patient;
import com.hospital.entity.User;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional
    @CacheEvict(value = "patients", allEntries = true)
    public Patient createOrUpdateProfile(Long userId, PatientProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Patient patient = patientRepository.findByUserId(userId)
                .orElse(Patient.builder().user(user).build());

        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setAddress(request.getAddress());
        patient.setEmergencyContact(request.getEmergencyContact());
        patient.setMedicalHistory(request.getMedicalHistory());

        return patientRepository.save(patient);
    }

    @Cacheable(value = "patients", key = "#userId")
    public Patient getPatientByUserId(Long userId) {
        return patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for userId: " + userId));
    }

    @Cacheable(value = "patients", key = "#id")
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }
}

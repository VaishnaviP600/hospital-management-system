package com.hospital.service.impl;

import com.hospital.entity.Doctor;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    @SuppressWarnings("unused") //
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @SuppressWarnings("unused") //
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + id));
    }

    @SuppressWarnings("unused") //
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    public Doctor getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for userId: " + userId));
    }
}

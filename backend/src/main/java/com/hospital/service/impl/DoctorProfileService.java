package com.hospital.service.impl;

import com.hospital.dto.request.DoctorProfileRequest;
import com.hospital.entity.Doctor;
import com.hospital.entity.User;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorProfileService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createOrUpdateProfile(Long userId, DoctorProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElse(Doctor.builder().user(user).build());

        doctor.setSpecialization(request.getSpecialization());
        doctor.setQualification(request.getQualification());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setExperienceYears(request.getExperienceYears());
        doctor.setDepartment(request.getDepartment());
        doctor.setConsultationFee(request.getConsultationFee());

        doctorRepository.save(doctor);
    }
}

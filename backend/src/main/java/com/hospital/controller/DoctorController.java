package com.hospital.controller;

import com.hospital.dto.request.DoctorProfileRequest;
import com.hospital.dto.response.ApiResponse;
import com.hospital.entity.Doctor;
import com.hospital.entity.User;
import com.hospital.service.impl.DoctorProfileService;
import com.hospital.service.impl.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorProfileService doctorProfileService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllDoctors() {
        List<Map<String, Object>> doctors = doctorService.getAllDoctors()
                .stream().map(this::toMap).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(doctors, "Doctors retrieved"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(toMap(doctorService.getDoctorById(id)), "Doctor retrieved"));
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getBySpecialization(@PathVariable String specialization) {
        List<Map<String, Object>> doctors = doctorService.getDoctorsBySpecialization(specialization)
                .stream().map(this::toMap).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(doctors, "Doctors retrieved"));
    }

    @PostMapping("/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Void>> createProfile(
            @AuthenticationPrincipal User user,
            @RequestBody DoctorProfileRequest request) {
        doctorProfileService.createOrUpdateProfile(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(null, "Profile saved successfully"));
    }

    private Map<String, Object> toMap(Doctor d) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", d.getId());
        map.put("specialization", d.getSpecialization() != null ? d.getSpecialization() : "");
        map.put("qualification", d.getQualification() != null ? d.getQualification() : "");
        map.put("department", d.getDepartment() != null ? d.getDepartment() : "");
        map.put("experienceYears", d.getExperienceYears() != null ? d.getExperienceYears() : 0);
        map.put("consultationFee", d.getConsultationFee() != null ? d.getConsultationFee() : 0);
        Map<String, Object> user = new HashMap<>();
        user.put("id", d.getUser().getId());
        user.put("firstName", d.getUser().getFirstName() != null ? d.getUser().getFirstName() : "");
        user.put("lastName", d.getUser().getLastName() != null ? d.getUser().getLastName() : "");
        user.put("email", d.getUser().getEmail() != null ? d.getUser().getEmail() : "");
        map.put("user", user);
        return map;
    }
}

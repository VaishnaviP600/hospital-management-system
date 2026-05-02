package com.hospital.controller;

import com.hospital.dto.request.PatientProfileRequest;
import com.hospital.dto.response.ApiResponse;
import com.hospital.entity.Patient;
import com.hospital.entity.User;
import com.hospital.service.impl.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Patients", description = "Patient profile management")
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/profile")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Create or update patient profile")
    public ResponseEntity<ApiResponse<Patient>> createOrUpdateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody PatientProfileRequest request) {
        Patient patient = patientService.createOrUpdateProfile(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(patient, "Profile updated"));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get current patient profile")
    public ResponseEntity<ApiResponse<Patient>> getMyProfile(
            @AuthenticationPrincipal User user) {
        Patient patient = patientService.getPatientByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.success(patient, "Profile retrieved"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all patients (Admin only)")
    public ResponseEntity<ApiResponse<List<Patient>>> getAllPatients() {
        return ResponseEntity.ok(ApiResponse.success(patientService.getAllPatients(), "Patients retrieved"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Get patient by ID")
    public ResponseEntity<ApiResponse<Patient>> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(patientService.getPatientById(id), "Patient retrieved"));
    }
}

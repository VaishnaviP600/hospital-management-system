package com.hospital.controller;

import com.hospital.dto.request.AppointmentRequest;
import com.hospital.dto.response.ApiResponse;
import com.hospital.dto.response.AppointmentResponse;
import com.hospital.entity.User;
import com.hospital.enums.AppointmentStatus;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.service.impl.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> bookAppointment(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse response = appointmentService.bookAppointment(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Appointment booked successfully"));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getMyAppointments(
            @AuthenticationPrincipal User user) {
        Long patientId = patientRepository.findByUserId(user.getId())
                .map(p -> p.getId())
                .orElse(null);
        if (patientId == null) return ResponseEntity.ok(ApiResponse.success(List.of(), "No appointments"));
        return ResponseEntity.ok(ApiResponse.success(
                appointmentService.getPatientAppointments(patientId), "Appointments retrieved"));
    }

    @GetMapping("/doctor/my")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getDoctorAppointments(
            @AuthenticationPrincipal User user) {
        Long doctorId = doctorRepository.findByUserId(user.getId())
                .map(d -> d.getId())
                .orElse(null);
        if (doctorId == null) return ResponseEntity.ok(ApiResponse.success(List.of(), "No appointments"));
        return ResponseEntity.ok(ApiResponse.success(
                appointmentService.getDoctorAppointments(doctorId), "Appointments retrieved"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {
        AppointmentResponse response = appointmentService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Status updated to " + status));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAllAppointments() {
        return ResponseEntity.ok(ApiResponse.success(
                appointmentService.getAllAppointments(), "All appointments retrieved"));
    }
}

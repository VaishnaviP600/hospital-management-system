package com.hospital.dto.response;

import com.hospital.enums.AppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialization;
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
    private String reason;
    private String notes;
    private LocalDateTime createdAt;
}

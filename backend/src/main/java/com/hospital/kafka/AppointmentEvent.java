package com.hospital.kafka;

import com.hospital.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentEvent implements Serializable {
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private String patientName;
    private String doctorName;
    private String patientEmail;
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
    private String reason;
    private LocalDateTime eventTimestamp;
}

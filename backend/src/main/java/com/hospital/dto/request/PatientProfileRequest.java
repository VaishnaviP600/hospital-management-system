package com.hospital.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientProfileRequest {
    private LocalDate dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String address;
    private String emergencyContact;
    private String medicalHistory;
}

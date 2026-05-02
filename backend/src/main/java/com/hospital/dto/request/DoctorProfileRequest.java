package com.hospital.dto.request;

import lombok.Data;

@Data
public class DoctorProfileRequest {
    private String specialization;
    private String qualification;
    private String licenseNumber;
    private Integer experienceYears;
    private String department;
    private Double consultationFee;
}

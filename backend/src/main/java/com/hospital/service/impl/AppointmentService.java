package com.hospital.service.impl;

import com.hospital.dto.request.AppointmentRequest;
import com.hospital.dto.response.AppointmentResponse;
import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;
import com.hospital.enums.AppointmentStatus;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.kafka.AppointmentEvent;
import com.hospital.kafka.AppointmentEventProducer;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentEventProducer eventProducer;

    @Transactional
    public AppointmentResponse bookAppointment(Long userId, AppointmentRequest request) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDateTime(request.getAppointmentDateTime())
                .reason(request.getReason())
                .status(AppointmentStatus.PENDING)
                .build();

        appointment = appointmentRepository.save(appointment);

        try {
            eventProducer.publishAppointmentEvent(AppointmentEvent.builder()
                    .appointmentId(appointment.getId())
                    .patientId(patient.getId())
                    .doctorId(doctor.getId())
                    .patientName(patient.getUser().getFirstName() + " " + patient.getUser().getLastName())
                    .doctorName(doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName())
                    .patientEmail(patient.getUser().getEmail())
                    .appointmentDateTime(appointment.getAppointmentDateTime())
                    .status(appointment.getStatus())
                    .reason(appointment.getReason())
                    .eventTimestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            // Kafka failure should not block booking
        }

        return mapToResponse(appointment);
    }

    public List<AppointmentResponse> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<AppointmentResponse> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse updateStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        appointment.setStatus(status);
        appointment = appointmentRepository.save(appointment);

        try {
            eventProducer.publishAppointmentEvent(AppointmentEvent.builder()
                    .appointmentId(appointment.getId())
                    .patientId(appointment.getPatient().getId())
                    .doctorId(appointment.getDoctor().getId())
                    .patientName(appointment.getPatient().getUser().getFirstName() + " " +
                                 appointment.getPatient().getUser().getLastName())
                    .doctorName(appointment.getDoctor().getUser().getFirstName() + " " +
                                appointment.getDoctor().getUser().getLastName())
                    .patientEmail(appointment.getPatient().getUser().getEmail())
                    .appointmentDateTime(appointment.getAppointmentDateTime())
                    .status(status)
                    .eventTimestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            // Kafka failure should not block status update
        }

        return mapToResponse(appointment);
    }

    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private AppointmentResponse mapToResponse(Appointment a) {
        return AppointmentResponse.builder()
                .id(a.getId())
                .patientId(a.getPatient().getId())
                .patientName(a.getPatient().getUser().getFirstName() + " " +
                             a.getPatient().getUser().getLastName())
                .doctorId(a.getDoctor().getId())
                .doctorName(a.getDoctor().getUser().getFirstName() + " " +
                            a.getDoctor().getUser().getLastName())
                .doctorSpecialization(a.getDoctor().getSpecialization())
                .appointmentDateTime(a.getAppointmentDateTime())
                .status(a.getStatus())
                .reason(a.getReason())
                .notes(a.getNotes())
                .createdAt(a.getCreatedAt())
                .build();
    }
}

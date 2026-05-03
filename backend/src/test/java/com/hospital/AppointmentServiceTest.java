package com.hospital;

import com.hospital.dto.request.AppointmentRequest;
import com.hospital.dto.response.AppointmentResponse;
import com.hospital.entity.*;
import com.hospital.enums.AppointmentStatus;
import com.hospital.enums.Role;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.kafka.AppointmentEventProducer;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.service.impl.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private AppointmentEventProducer eventProducer;

    @InjectMocks private AppointmentService appointmentService;

    private User patientUser, doctorUser;
    private Patient patient;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        patientUser = User.builder().id(1L).firstName("Jane").lastName("Doe")
                .email("jane@example.com").role(Role.PATIENT).build();
        doctorUser = User.builder().id(2L).firstName("Dr").lastName("Smith")
                .email("smith@example.com").role(Role.DOCTOR).build();
        patient = Patient.builder().id(1L).user(patientUser).build();
        doctor = Doctor.builder().id(1L).user(doctorUser).specialization("Cardiology").build();
    }

    @Test
    void bookAppointment_Success() {
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setAppointmentDateTime(LocalDateTime.now().plusDays(1));
        request.setReason("Chest pain");

        Appointment saved = Appointment.builder()
                .id(1L).patient(patient).doctor(doctor)
                .appointmentDateTime(request.getAppointmentDateTime())
                .status(AppointmentStatus.PENDING)
                .reason(request.getReason())
                .createdAt(LocalDateTime.now())
                .build();

        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any())).thenReturn(saved);
        doNothing().when(eventProducer).publishAppointmentEvent(any());

        AppointmentResponse response = appointmentService.bookAppointment(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        assertThat(response.getDoctorName()).contains("Smith");
    }

    @Test
    void bookAppointment_PatientNotFound_ThrowsException() {
        when(patientRepository.findByUserId(999L)).thenReturn(Optional.empty());

        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setAppointmentDateTime(LocalDateTime.now().plusDays(1));

        assertThatThrownBy(() -> appointmentService.bookAppointment(999L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient profile not found");
    }

    @Test
    void bookAppointment_DoctorNotFound_ThrowsException() {
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(999L);
        request.setAppointmentDateTime(LocalDateTime.now().plusDays(1));

        assertThatThrownBy(() -> appointmentService.bookAppointment(1L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Doctor not found");
    }

    @Test
    void updateStatus_Success() {
        Appointment appointment = Appointment.builder()
                .id(1L).patient(patient).doctor(doctor)
                .appointmentDateTime(LocalDateTime.now().plusDays(1))
                .status(AppointmentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenReturn(appointment);
        doNothing().when(eventProducer).publishAppointmentEvent(any());

        AppointmentResponse response = appointmentService.updateStatus(1L, AppointmentStatus.CONFIRMED);
        assertThat(response).isNotNull();
    }

    @Test
    void getPatientAppointments_ReturnsEmptyList() {
        when(appointmentRepository.findByPatientId(1L)).thenReturn(List.of());
        List<AppointmentResponse> result = appointmentService.getPatientAppointments(1L);
        assertThat(result).isEmpty();
    }
}

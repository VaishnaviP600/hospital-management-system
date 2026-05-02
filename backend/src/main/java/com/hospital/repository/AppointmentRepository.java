package com.hospital.repository;

import com.hospital.entity.Appointment;
import com.hospital.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByStatus(AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDateTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndDateRange(Long doctorId,
                                                  LocalDateTime start,
                                                  LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId " +
           "AND a.status = :status")
    List<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status);

    boolean existsByDoctorIdAndAppointmentDateTime(Long doctorId, LocalDateTime dateTime);
}

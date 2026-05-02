package com.hospital.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AppointmentEventConsumer {

    @KafkaListener(topics = "appointment-events", groupId = "hospital-group")
    public void consumeAppointmentEvent(AppointmentEvent event) {
        log.info("Received appointment event: appointmentId={}, status={}, patient={}, doctor={}",
                event.getAppointmentId(),
                event.getStatus(),
                event.getPatientName(),
                event.getDoctorName());

        switch (event.getStatus()) {
            case CONFIRMED -> handleConfirmed(event);
            case CANCELLED -> handleCancelled(event);
            case COMPLETED -> handleCompleted(event);
            default -> log.info("No action for status: {}", event.getStatus());
        }
    }

    private void handleConfirmed(AppointmentEvent event) {
        log.info("NOTIFICATION -> Appointment #{} CONFIRMED for {} with Dr. {} on {}",
                event.getAppointmentId(),
                event.getPatientName(),
                event.getDoctorName(),
                event.getAppointmentDateTime());
        // In production: send email/SMS via notification service
    }

    private void handleCancelled(AppointmentEvent event) {
        log.info("NOTIFICATION -> Appointment #{} CANCELLED for {} with Dr. {}",
                event.getAppointmentId(),
                event.getPatientName(),
                event.getDoctorName());
        // In production: send cancellation email/SMS
    }

    private void handleCompleted(AppointmentEvent event) {
        log.info("NOTIFICATION -> Appointment #{} COMPLETED for {}",
                event.getAppointmentId(),
                event.getPatientName());
    }
}

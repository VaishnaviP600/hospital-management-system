package com.hospital.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentEventProducer {

    private static final String TOPIC = "appointment-events";
    private final KafkaTemplate<String, AppointmentEvent> kafkaTemplate;

    public void publishAppointmentEvent(AppointmentEvent event) {
        log.info("Publishing appointment event: appointmentId={}, status={}",
                event.getAppointmentId(), event.getStatus());
        kafkaTemplate.send(TOPIC, String.valueOf(event.getAppointmentId()), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Event published successfully to topic={}, partition={}, offset={}",
                                TOPIC,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to publish event for appointmentId={}: {}",
                                event.getAppointmentId(), ex.getMessage());
                    }
                });
    }
}

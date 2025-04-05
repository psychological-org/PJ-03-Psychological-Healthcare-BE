package com.microservices.appointment.appointment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Appointment {

    @Id
    @GeneratedValue
    private Integer id;
    private String status;
    private String appointmentDate;
    private String appointmentTime;
    private Double rating;

    private Integer patientId;
    private Integer doctorId;

}
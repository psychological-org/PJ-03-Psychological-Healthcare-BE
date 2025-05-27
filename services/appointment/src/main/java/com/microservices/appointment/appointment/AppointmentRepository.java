package com.microservices.appointment.appointment;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    @Query("SELECT u FROM Appointment u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<Appointment> findOneById(Integer id);

    @Query("SELECT u FROM Appointment u WHERE u.deletedAt IS NULL")
    public Page<Appointment> findAllUsers(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Appointment u SET u.deletedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    void softDeleteById(Integer id);
}

INSERT INTO appointment (
    status, appointment_date, appointment_time, rating, review,
    patient_id, doctor_id
) VALUES
      ('PENDING', '2025-05-15', '10:30:00',
       NULL, NULL, '1a6f5b2f-6e14-4e18-8a51-bb63cb740011',
       '0e370c47-9a29-4a8e-8f17-4e473d68cadd'),
      ('CONFIRMED', '2025-05-16',
       '14:00:00', 4.5, 'Bác sĩ rất nhiệt tình và chu đáo.',
       '1a6f5b2f-6e14-4e18-8a51-bb63cb740011', '0e370c47-9a29-4a8e-8f17-4e473d68cadd'),
      ('CANCELLED', '2025-05-17',
       '09:00:00', NULL, NULL,
       '1a6f5b2f-6e14-4e18-8a51-bb63cb740011', '0e370c47-9a29-4a8e-8f17-4e473d68cadd');

CREATE TABLE IF NOT EXISTS appointment (
      id SERIAL PRIMARY KEY,
      status VARCHAR(255),
      appointment_date VARCHAR(255),
      appointment_time VARCHAR(255),
      rating FLOAT,
      patient_id INTEGER,
      doctor_id INTEGER,
      created_at TIMESTAMP,
      updated_at TIMESTAMP,
      deleted_at TIMESTAMP
);


ALTER TABLE appointment
    ALTER COLUMN patient_id TYPE VARCHAR(255),
    ALTER COLUMN doctor_id TYPE VARCHAR(255);
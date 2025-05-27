CREATE TABLE follow (
       id SERIAL PRIMARY KEY,
       status VARCHAR(255) NOT NULL,
       sender_id VARCHAR(255) NOT NULL,
       receiver_id VARCHAR(255) NOT NULL,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       deleted_at TIMESTAMP DEFAULT NULL
);

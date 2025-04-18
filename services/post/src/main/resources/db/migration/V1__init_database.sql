CREATE TABLE POST (
      id SERIAL PRIMARY KEY,
      content VARCHAR(1000) NOT NULL,
      user_id VARCHAR(255) NOT NULL,
      community_id INT,
      visibility VARCHAR(255) NOT NULL,
      image_url VARCHAR(255),
      react_count INT,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      deleted_at TIMESTAMP DEFAULT NULL
);

CREATE TABLE comment (
      id SERIAL PRIMARY KEY,
      content VARCHAR(255) NOT NULL,
      image_url VARCHAR(255),
      user_id VARCHAR(255),
      post_id INTEGER,
      react_count INTEGER,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      deleted_at TIMESTAMP DEFAULT NULL
);


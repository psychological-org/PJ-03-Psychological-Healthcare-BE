CREATE TABLE LIKE_POST (
                  id SERIAL PRIMARY KEY,
                    post_id INT NOT NULL,
                  user_id VARCHAR(255) NOT NULL,
                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  deleted_at TIMESTAMP DEFAULT NULL
);

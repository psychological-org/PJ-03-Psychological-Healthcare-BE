INSERT INTO follow (status, sender_id, receiver_id, created_at, updated_at)
VALUES
-- Đã chấp nhận theo dõi
('accepted', '0e370c47-9a29-4a8e-8f17-4e473d68cadd', '1a6f5b2f-6e14-4e18-8a51-bb63cb740011', NOW(), NOW()),

-- Đang chờ phê duyệt
('pending', '1a6f5b2f-6e14-4e18-8a51-bb63cb740011', '0e370c47-9a29-4a8e-8f17-4e473d68cadd', NOW(), NOW())


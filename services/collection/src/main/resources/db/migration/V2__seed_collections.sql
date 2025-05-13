INSERT INTO collection (name, type, resource_url, topic_id, created_at, updated_at)
VALUES
    (
        'Thiền Căn Bản cho Người Mới',
        'PODCAST',
        'https://youtu.be/r3gRcVd1swk',
        1,
        NOW(),
        NOW()
    ),
    (
        'Hướng Dẫn Hít Thở Sâu và Thư Giãn',
        'PODCAST',
        'https://youtu.be/QU_ZXKlBk9I',
        2,
        NOW(),
        NOW()
    ),
    (
        'Tài liệu về Liệu pháp CBT cho lo âu',
        'MUSIC',
        'https://youtu.be/ciqJpsyWAB0',
        3,
        NOW(),
        NOW()
    );

INSERT INTO collection_seen (user_id, collection_id, created_at, updated_at)
VALUES
-- Người dùng A đã xem collection số 1
('0e370c47-9a29-4a8e-8f17-4e473d68cadd', 1, NOW(), NOW()),

-- Người dùng B đã xem collection số 2
('0e370c47-9a29-4a8e-8f17-4e473d68cadd', 2, NOW(), NOW()),

-- Người dùng C đã xem collection số 3
('1a6f5b2f-6e14-4e18-8a51-bb63cb740011', 3, NOW(), NOW())
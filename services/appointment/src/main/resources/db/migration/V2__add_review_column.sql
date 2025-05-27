-- Thêm cột review
ALTER TABLE appointment ADD COLUMN review TEXT;

-- Chuyển đổi kiểu dữ liệu
ALTER TABLE appointment
ALTER COLUMN appointment_date TYPE DATE
        USING appointment_date::DATE;
ALTER TABLE appointment
ALTER COLUMN appointment_time TYPE TIME
        USING appointment_time::TIME;


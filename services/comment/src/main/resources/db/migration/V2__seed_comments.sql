INSERT INTO comment (content, image_url, user_id, post_id, react_count, created_at, updated_at)
VALUES
    (
        'Bài viết rất hữu ích! Cảm ơn bạn đã chia sẻ 💡',
        NULL,
        '0e370c47-9a29-4a8e-8f17-4e473d68cadd',
        1,
        5,
        NOW(),
        NOW()
    ),
    (
        'Mình đã thử phương pháp thiền bạn nói, cảm thấy tốt hơn rất nhiều 🙏',
        NULL,
        '1a6f5b2f-6e14-4e18-8a51-bb63cb740011',
        2,
        8,
        NOW(),
        NOW()
    ),
    (
        'Mình nghĩ thêm vài hình ảnh minh họa sẽ khiến bài viết dễ hiểu hơn nữa đó!',
        'https://img.pikbest.com/wp/202433/illustration-woman-face-black-icon-vector_10640021.jpg!w700wp',
        '0e370c47-9a29-4a8e-8f17-4e473d68cadd',
        3,
        2,
        NOW(),
        NOW()
    );

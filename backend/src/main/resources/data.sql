INSERT INTO users (username, email, password_hash, is_verified, created_at)
VALUES (
    'Ahmick',
    'ahmick@email.com',
    '$2a$10$yGiSNbNBLI9JeNlTwdXUoOrl0p1GwBLwRttoXWfOYwN4cNy5TTvQK',
    true,
    NOW()
) ON CONFLICT (email) DO NOTHING;

INSERT INTO users (username, email, password_hash, is_verified, created_at)
VALUES (
    'Ahmick3',
    'dogih61023@preparmy.com',
    '$2a$10$yGiSNbNBLI9JeNlTwdXUoOrl0p1GwBLwRttoXWfOYwN4cNy5TTvQK',
    true,
    NOW()
) ON CONFLICT (email) DO NOTHING;


INSERT INTO trips (user_id, name, description, start_date, end_date, cover_image_path, created_at)
VALUES 
(
    (SELECT id FROM users WHERE email = 'ahmick@email.com'),
    'Japan Trip 2026',
    'Exploring Tokyo, Kyoto and Osaka',
    '2026-03-15',
    '2026-03-28',
    NULL,
    NOW()
),
(
    (SELECT id FROM users WHERE email = 'ahmick@email.com'),
    'New Zealand Road Trip',
    'South Island adventure',
    '2026-07-01',
    '2026-07-14',
    NULL,
    NOW()
),
(
    (SELECT id FROM users WHERE email = 'ahmick@email.com'),
    'Sydney Weekend',
    'Quick weekend getaway',
    '2026-09-05',
    '2026-09-07',
    NULL,
    NOW()
) ON CONFLICT DO NOTHING;
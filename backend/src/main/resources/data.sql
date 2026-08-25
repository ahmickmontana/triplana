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

INSERT INTO trip_days (trip_id, date, day_number)
SELECT 
    (SELECT id FROM trips WHERE name = 'Japan Trip 2026' AND user_id = (SELECT id FROM users WHERE email = 'ahmick@email.com')),
    '2026-03-15'::date + (n - 1),
    n
FROM generate_series(1, 14) AS n
ON CONFLICT DO NOTHING;

INSERT INTO accommodations (trip_id, name, location_name, check_in_date, check_out_date, created_at)
VALUES
(
    (SELECT id FROM trips WHERE name = 'Japan Trip 2026' AND user_id = (SELECT id FROM users WHERE email = 'ahmick@email.com')),
    'Shinjuku Granbell Hotel',
    'Shinjuku, Tokyo, Japan',
    '2026-03-15',
    '2026-03-17',
    NOW()
),
(
    (SELECT id FROM trips WHERE name = 'Japan Trip 2026' AND user_id = (SELECT id FROM users WHERE email = 'ahmick@email.com')),
    'AirBnb',
    'Kyoto, Japan',
    '2026-03-18',
    '2026-03-19',
    NOW()
),
(
    (SELECT id FROM trips WHERE name = 'Japan Trip 2026' AND user_id = (SELECT id FROM users WHERE email = 'ahmick@email.com')),
    'APA Hotel Kyoto Station',
    'Kyoto, Japan',
    '2026-03-20',
    '2026-03-22',
    NOW()
),
(
    (SELECT id FROM trips WHERE name = 'Japan Trip 2026' AND user_id = (SELECT id FROM users WHERE email = 'ahmick@email.com')),
    'Cross Hotel Osaka',
    'Shinsaibashi, Osaka, Japan',
    '2026-03-22',
    '2026-03-24',
    NOW()
),
(
    (SELECT id FROM trips WHERE name = 'Japan Trip 2026' AND user_id = (SELECT id FROM users WHERE email = 'ahmick@email.com')),
    'Kansai Airport Hotel',
    'Kansai Airport, Osaka, Japan',
    '2026-03-25',
    '2026-03-28',
    NOW()
);
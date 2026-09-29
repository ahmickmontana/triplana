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
    'Japan Trip 2027',
    'Exploring Tokyo, Kyoto and Osaka',
    '2027-03-15',
    '2027-03-28',
    NULL,
    NOW()
),
(
    (SELECT id FROM users WHERE email = 'ahmick@email.com'),
    'New Zealand Road Trip',
    'South Island adventure',
    '2027-07-01',
    '2027-07-14',
    NULL,
    NOW()
),
(
    (SELECT id FROM users WHERE email = 'ahmick@email.com'),
    'Sydney Weekend',
    'Quick weekend getaway',
    '2027-09-05',
    '2027-09-07',
    NULL,
    NOW()
) ON CONFLICT DO NOTHING;

INSERT INTO trip_days (trip_id, date, day_number)
SELECT 
    (SELECT id FROM trips WHERE name = 'Japan Trip 2027' AND user_id = (SELECT id FROM users WHERE email = 'ahmick@email.com')),
    '2027-03-15'::date + (n - 1),
    n
FROM generate_series(1, 14) AS n
ON CONFLICT DO NOTHING;

INSERT INTO accommodations (trip_id, name, location_name, latitude, longitude, google_place_id, check_in_date, check_out_date, created_at)
VALUES
(
    (SELECT id FROM trips WHERE name = 'Japan Trip 2027' AND user_id = (SELECT id FROM users WHERE email = 'ahmick@email.com')),
    'Shinjuku Granbell Hotel',
    '2-14-5 Kabukicho, Shinjuku, Tokyo 160-0021, Japan',
    35.6938,
    139.7036,
    'ChIJ6Rn4VeSLGGARSCd5bEOtaOQ',
    '2027-03-15',
    '2027-03-17',
    NOW()
),
(
    (SELECT id FROM trips WHERE name = 'Japan Trip 2027' AND user_id = (SELECT id FROM users WHERE email = 'ahmick@email.com')),
    'AirBnb',
    'Kyoto, Japan',
    NULL,
    NULL,
    NULL,
    '2027-03-18',
    '2027-03-19',
    NOW()
),
(
    (SELECT id FROM trips WHERE name = 'Japan Trip 2027' AND user_id = (SELECT id FROM users WHERE email = 'ahmick@email.com')),
    'APA Hotel Kyoto Station',
    'Kyoto, Japan',
    NULL,
    NULL,
    NULL,
    '2027-03-20',
    '2027-03-22',
    NOW()
),
(
    (SELECT id FROM trips WHERE name = 'Japan Trip 2027' AND user_id = (SELECT id FROM users WHERE email = 'ahmick@email.com')),
    'Cross Hotel Osaka',
    'Shinsaibashi, Osaka, Japan',
    NULL,
    NULL,
    NULL,
    '2027-03-22',
    '2027-03-24',
    NOW()
),
(
    (SELECT id FROM trips WHERE name = 'Japan Trip 2027' AND user_id = (SELECT id FROM users WHERE email = 'ahmick@email.com')),
    'Kansai Airport Hotel',
    'Kansai Airport, Osaka, Japan',
    NULL,
    NULL,
    NULL,
    '2027-03-25',
    '2027-03-28',
    NOW()
);

INSERT INTO activities (trip_day_id, title, location_name, latitude, longitude, google_place_id, manual_order, created_at)
VALUES
(
    (SELECT id FROM trip_days WHERE trip_id = (SELECT id FROM trips WHERE name = 'Japan Trip 2027' AND user_id = (SELECT id FROM users WHERE email = 'ahmick@email.com')) AND day_number = 1),
    'Shibuya Sky',
    '2-24-12 Shibuya, Shibuya City, Tokyo 150-6145, Japan',
    35.6586719,
    139.7019848,
    'ChIJhxxszamMGGARcuAXpFunolU',
    1,
    NOW()
),
(
    (SELECT id FROM trip_days WHERE trip_id = (SELECT id FROM trips WHERE name = 'Japan Trip 2027' AND user_id = (SELECT id FROM users WHERE email = 'ahmick@email.com')) AND day_number = 1),
    'Hachiko Statue',
    'Shibuya, Tokyo 150-0043, Japan',
    35.659082,
    139.700451,
    'ChIJ6Rn4VeSLGGARSCd5bEOtaOQ',
    2,
    NOW()
),
(
    (SELECT id FROM trip_days WHERE trip_id = (SELECT id FROM trips WHERE name = 'Japan Trip 2027' AND user_id = (SELECT id FROM users WHERE email = 'ahmick@email.com')) AND day_number = 1),
    'Tokyo Disneyland',
    '1-1 Maihama, Urayasu, Chiba 279-0031, Japan',
    35.6329,
    139.8804,
    'ChIJ_xQKBjtsGGARXyVTp_3zMZI',
    3,
    NOW()
);
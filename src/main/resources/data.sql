-- USERS
INSERT INTO users (first_name, last_name, username, password, is_active)
VALUES ('Ali', 'Veli', 'ali.veli', '1234', true),
       ('Ayşe', 'Yılmaz', 'ayse.yilmaz', 'abcd', true),
       ('Mehmet', 'Demir', 'mehmet.demir', 'pass', true),
       ('Deniz', 'Sarı', 'deniz.sari', '1111', true),
       ('Gizem', 'Kara', 'gizem.kara', 'qwerty', true),
       ('Burak', 'Aslan', 'burak.aslan', 'burak123', true),
       ('Selin', 'Toprak', 'selin.toprak', 'selinpw', true),
       ('Emre', 'Polat', 'emre.polat', 'empw', false),
       ('Zeynep', 'Aydın', 'zeynep.aydin', 'zeynep2024', true);

-- TRAINING_TYPE
INSERT INTO training_types (training_type_name)
VALUES ('Cardio'),
       ('Strength'),
       ('Yoga'),
       ('Pilates'),
       ('HIIT');

-- TRAINEE
INSERT INTO trainees (user_id, date_of_birth, address)
VALUES ((SELECT id FROM users WHERE username = 'ali.veli'), '2000-01-01', 'Istanbul'),
       ((SELECT id FROM users WHERE username = 'mehmet.demir'), '1995-05-10', 'Ankara'),
       ((SELECT id FROM users WHERE username = 'deniz.sari'), '2001-03-15', 'Izmir'),
       ((SELECT id FROM users WHERE username = 'gizem.kara'), '1997-09-21', 'Bursa'),
       ((SELECT id FROM users WHERE username = 'emre.polat'), '1993-11-12', 'Antalya'),
       ((SELECT id FROM users WHERE username = 'zeynep.aydin'), '2002-07-18', 'Eskişehir');

-- TRAINER
INSERT INTO trainers (user_id, specialization)
VALUES ((SELECT id FROM users WHERE username = 'ayse.yilmaz'),
        (SELECT id FROM training_types WHERE training_type_name = 'Cardio')),
       ((SELECT id FROM users WHERE username = 'burak.aslan'),
        (SELECT id FROM training_types WHERE training_type_name = 'Strength')),
       ((SELECT id FROM users WHERE username = 'selin.toprak'),
        (SELECT id FROM training_types WHERE training_type_name = 'Yoga')),
       ((SELECT id FROM users WHERE username = 'gizem.kara'),
        (SELECT id FROM training_types WHERE training_type_name = 'Pilates'));

-- TRAINING
INSERT INTO trainings (trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration)
VALUES ((SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'ali.veli')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'ayse.yilmaz')),
        'Morning Cardio', (SELECT id FROM training_types WHERE training_type_name = 'Cardio'),
        '2025-07-22 09:00:00', 60),
       ((SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'mehmet.demir')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'burak.aslan')),
        'Strength Basics', (SELECT id FROM training_types WHERE training_type_name = 'Strength'),
        '2025-07-22 10:00:00', 45),
       ((SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'deniz.sari')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'selin.toprak')),
        'Yoga for Beginners', (SELECT id FROM training_types WHERE training_type_name = 'Yoga'),
        '2025-07-23 08:30:00', 50),
       ((SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'gizem.kara')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'gizem.kara')),
        'Pilates One-on-One', (SELECT id FROM training_types WHERE training_type_name = 'Pilates'),
        '2025-07-23 15:00:00', 40),
       ((SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'emre.polat')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'ayse.yilmaz')),
        'HIIT Rush', (SELECT id FROM training_types WHERE training_type_name = 'HIIT'),
        '2025-07-24 17:00:00', 35),
       ((SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'zeynep.aydin')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'selin.toprak')),
        'Yoga Stretch', (SELECT id FROM training_types WHERE training_type_name = 'Yoga'),
        '2025-07-25 19:00:00', 55),
       ((SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'ali.veli')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'burak.aslan')),
        'Strength Mix', (SELECT id FROM training_types WHERE training_type_name = 'Strength'),
        '2025-07-25 18:00:00', 45);

-- TRAINEE_TRAINER
INSERT INTO trainee_trainer (trainee_id, trainer_id)
VALUES ((SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'ali.veli')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'ayse.yilmaz'))),
       ((SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'ali.veli')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'burak.aslan'))),
       ((SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'mehmet.demir')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'burak.aslan'))),
       ((SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'deniz.sari')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'selin.toprak'))),
       ((SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'gizem.kara')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'gizem.kara'))),
       ((SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'emre.polat')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'ayse.yilmaz'))),
       ((SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'zeynep.aydin')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'selin.toprak')));

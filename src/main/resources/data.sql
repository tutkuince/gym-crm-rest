-- USERS
-- Insert initial users without specifying IDs (IDs will be auto-generated)
INSERT INTO users (first_name, last_name, username, password, is_active)
VALUES 
    ('Ali', 'Veli', 'ali.veli', '1234', true),
    ('Ayşe', 'Yılmaz', 'ayse.yilmaz', 'abcd', true),
    ('Mehmet', 'Demir', 'mehmet.demir', 'pass', true);

-- TRAINING_TYPE
-- Insert training types without IDs (IDs auto-generated)
INSERT INTO training_types (training_type_name)
VALUES 
    ('Cardio'),
    ('Strength'),
    ('Yoga');

-- TRAINEE
-- Insert trainees. user_id is selected based on username.
INSERT INTO trainees (user_id, date_of_birth, address)
VALUES 
    ((SELECT id FROM users WHERE username = 'ali.veli'), '2000-01-01', 'Istanbul'),
    ((SELECT id FROM users WHERE username = 'mehmet.demir'), '1995-05-10', 'Ankara');

-- TRAINER
-- Insert trainers. user_id is selected based on username.
INSERT INTO trainers (user_id, specialization)
VALUES 
    ((SELECT id FROM users WHERE username = 'ayse.yilmaz'), 'Fitness');

-- TRAINING
-- Insert trainings. FK values are selected dynamically from other tables.
INSERT INTO trainings (trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration)
VALUES 
    (
        (SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'ali.veli')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'ayse.yilmaz')),
        'Morning Cardio',
        (SELECT id FROM training_types WHERE training_type_name = 'Cardio'),
        '2025-07-22 09:00:00',
        60
    ),
    (
        (SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'mehmet.demir')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'ayse.yilmaz')),
        'Strength Basics',
        (SELECT id FROM training_types WHERE training_type_name = 'Strength'),
        '2025-07-22 10:00:00',
        45
    );

-- TRAINEE_TRAINER
-- Insert many-to-many relationships between trainees and trainers
INSERT INTO trainee_trainer (trainee_id, trainer_id)
VALUES 
    (
        (SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'ali.veli')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'ayse.yilmaz'))
    ),
    (
        (SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'mehmet.demir')),
        (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'ayse.yilmaz'))
    );
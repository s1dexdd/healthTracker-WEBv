-- Создаем таблицы только если их нет
CREATE TABLE IF NOT EXISTS "USERS" (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    height_cm INT,
    start_weight_kg DECIMAL(5,2),
    target_weight_kg DECIMAL(5,2),
    age INT,
    gender VARCHAR(10),
    activity_level VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS "FOOD_LOG" (
    food_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES "USERS"(user_id),
    log_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(255),
    meal_type VARCHAR(50),
    calories_per_100g INT,
    portion_size_grams INT,
    protein_per_100g DECIMAL(5,2),
    fats_per_100g DECIMAL(5,2),
    carbs_per_100g DECIMAL(5,2),
    calories INT,
    protein_g DECIMAL(5,2),
    fats_g DECIMAL(5,2),
    carbs_g DECIMAL(5,2)
);

CREATE TABLE IF NOT EXISTS "WORKOUT_LOG" (
    workout_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES "USERS"(user_id),
    log_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    type VARCHAR(255),
    duration_minutes INT,
    calories_burned_per_minute DECIMAL(5,2),
    calories_burned INT
);

-- Важно: создаем дефолтного юзера, чтобы запросы не падали
INSERT INTO "USERS" (user_id, name, height_cm, start_weight_kg, target_weight_kg, age, gender, activity_level)
SELECT 1, 'Admin', 180, 80, 75, 25, 'MALE', 'MID'
WHERE NOT EXISTS (SELECT 1 FROM "USERS" WHERE user_id = 1);
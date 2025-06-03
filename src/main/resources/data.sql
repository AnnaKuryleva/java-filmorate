DELETE FROM likes;
DELETE FROM friendship;
DELETE FROM genre_id_film_id;
DELETE FROM films;
DELETE FROM users;
DELETE FROM genres;
DELETE FROM rating;

-- Заполнение таблицы rating
INSERT INTO rating (name)
SELECT 'G' WHERE NOT EXISTS (SELECT 1 FROM rating WHERE name = 'G');
INSERT INTO rating (name)
SELECT 'PG' WHERE NOT EXISTS (SELECT 1 FROM rating WHERE name = 'PG');
INSERT INTO rating (name)
SELECT 'PG-13' WHERE NOT EXISTS (SELECT 1 FROM rating WHERE name = 'PG-13');
INSERT INTO rating (name)
SELECT 'R' WHERE NOT EXISTS (SELECT 1 FROM rating WHERE name = 'R');
INSERT INTO rating (name)
SELECT 'NC-17' WHERE NOT EXISTS (SELECT 1 FROM rating WHERE name = 'NC-17');

-- Заполнение таблицы genres
INSERT INTO genres (name)
SELECT 'Комедия' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Комедия');
INSERT INTO genres (name)
SELECT 'Драма' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Драма');
INSERT INTO genres (name)
SELECT 'Боевик' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Боевик');
INSERT INTO genres (name)
SELECT 'Триллер' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Триллер');
INSERT INTO genres (name)
SELECT 'Фантастика' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Фантастика');

-- Заполнение таблицы users
INSERT INTO users (email, login, name, birthday)
SELECT 'ivanov@example.com', 'ivanov', 'Иван', '1990-01-01'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'ivanov@example.com');
INSERT INTO users (email, login, name, birthday)
SELECT 'petrova@example.com', 'petrova', 'Елена', '1985-06-15'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'petrova@example.com');
INSERT INTO users (email, login, name, birthday)
SELECT 'sidorov@example.com', 'sidorov', 'Алексей', '1995-03-22'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'sidorov@example.com');

-- Заполнение таблицы films
INSERT INTO films (name, description, release_date, duration, rating_id)
SELECT 'Брат', 'История о брате', '1997-12-12', 96,
       (SELECT rating_id FROM rating WHERE name = 'R')
WHERE NOT EXISTS (SELECT 1 FROM films WHERE name = 'Брат');
INSERT INTO films (name, description, release_date, duration, rating_id)
SELECT 'Сталкер', 'Таинственное путешествие', '1979-05-25', 162,
       (SELECT rating_id FROM rating WHERE name = 'PG-13')
WHERE NOT EXISTS (SELECT 1 FROM films WHERE name = 'Сталкер');
INSERT INTO films (name, description, release_date, duration, rating_id)
SELECT 'Ёлки', 'Новогодние истории', '2010-12-16', 90,
       (SELECT rating_id FROM rating WHERE name = 'PG')
WHERE NOT EXISTS (SELECT 1 FROM films WHERE name = 'Ёлки');

-- Заполнение таблицы genre_id_film_id
INSERT INTO genre_id_film_id (film_id, genre_id)
SELECT (SELECT id FROM films WHERE name = 'Брат'),
       (SELECT genre_id FROM genres WHERE name = 'Драма')
WHERE NOT EXISTS (SELECT 1 FROM genre_id_film_id
                  WHERE film_id = (SELECT id FROM films WHERE name = 'Брат')
                    AND genre_id = (SELECT genre_id FROM genres WHERE name = 'Драма'));
INSERT INTO genre_id_film_id (film_id, genre_id)
SELECT (SELECT id FROM films WHERE name = 'Сталкер'),
       (SELECT genre_id FROM genres WHERE name = 'Фантастика')
WHERE NOT EXISTS (SELECT 1 FROM genre_id_film_id
                  WHERE film_id = (SELECT id FROM films WHERE name = 'Сталкер')
                    AND genre_id = (SELECT genre_id FROM genres WHERE name = 'Фантастика'));
INSERT INTO genre_id_film_id (film_id, genre_id)
SELECT (SELECT id FROM films WHERE name = 'Сталкер'),
       (SELECT genre_id FROM genres WHERE name = 'Драма')
WHERE NOT EXISTS (SELECT 1 FROM genre_id_film_id
                  WHERE film_id = (SELECT id FROM films WHERE name = 'Сталкер')
                    AND genre_id = (SELECT genre_id FROM genres WHERE name = 'Драма'));
INSERT INTO genre_id_film_id (film_id, genre_id)
SELECT (SELECT id FROM films WHERE name = 'Ёлки'),
       (SELECT genre_id FROM genres WHERE name = 'Комедия')
WHERE NOT EXISTS (SELECT 1 FROM genre_id_film_id
                  WHERE film_id = (SELECT id FROM films WHERE name = 'Ёлки')
                    AND genre_id = (SELECT genre_id FROM genres WHERE name = 'Комедия'));

-- Заполнение таблицы likes
INSERT INTO likes (film_id, user_id)
SELECT (SELECT id FROM films WHERE name = 'Брат'),
       (SELECT id FROM users WHERE login = 'ivanov')
WHERE NOT EXISTS (SELECT 1 FROM likes
                  WHERE film_id = (SELECT id FROM films WHERE name = 'Брат')
                    AND user_id = (SELECT id FROM users WHERE login = 'ivanov'));
INSERT INTO likes (film_id, user_id)
SELECT (SELECT id FROM films WHERE name = 'Брат'),
       (SELECT id FROM users WHERE login = 'petrova')
WHERE NOT EXISTS (SELECT 1 FROM likes
                  WHERE film_id = (SELECT id FROM films WHERE name = 'Брат')
                    AND user_id = (SELECT id FROM users WHERE login = 'petrova'));
INSERT INTO likes (film_id, user_id)
SELECT (SELECT id FROM films WHERE name = 'Сталкер'),
       (SELECT id FROM users WHERE login = 'sidorov')
WHERE NOT EXISTS (SELECT 1 FROM likes
                  WHERE film_id = (SELECT id FROM films WHERE name = 'Сталкер')
                    AND user_id = (SELECT id FROM users WHERE login = 'sidorov'));

-- Заполнение таблицы friendship
INSERT INTO friendship (inviter_id, acceptor_id, confirmation_status)
SELECT (SELECT id FROM users WHERE login = 'ivanov'),
       (SELECT id FROM users WHERE login = 'petrova'),
       TRUE
WHERE NOT EXISTS (SELECT 1 FROM friendship
                  WHERE inviter_id = (SELECT id FROM users WHERE login = 'ivanov')
                    AND acceptor_id = (SELECT id FROM users WHERE login = 'petrova'));
INSERT INTO friendship (inviter_id, acceptor_id, confirmation_status)
SELECT (SELECT id FROM users WHERE login = 'petrova'),
       (SELECT id FROM users WHERE login = 'sidorov'),
       FALSE
WHERE NOT EXISTS (SELECT 1 FROM friendship
                  WHERE inviter_id = (SELECT id FROM users WHERE login = 'petrova')
                    AND acceptor_id = (SELECT id FROM users WHERE login = 'sidorov'));
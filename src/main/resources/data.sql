-- Очистка таблиц (если нужно)
DELETE FROM genre_id_film_id;
DELETE FROM likes;
DELETE FROM friendship;
DELETE FROM films;
DELETE FROM users;
DELETE FROM genres;
DELETE FROM rating;

-- Вставка рейтингов с фиксированными ID
MERGE INTO rating (rating_id, name) KEY (rating_id)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

-- Вставка жанров с фиксированными ID
MERGE INTO genres (genre_id, name) KEY (genre_id)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Фантастика'),
           (5, 'Триллер'),
           (6, 'Боевик');
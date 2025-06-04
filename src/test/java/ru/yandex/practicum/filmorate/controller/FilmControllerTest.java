package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Arrays;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тестовый класс для проверки функциональности {@link FilmController}.
 * Класс содержит тесты для проверки операций с фильмами,
 * таких как создание и обновление фильмов, с использованием Spring Boot и MockMvc.
 */

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    private Film film;
    private User user;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate("2001-01-01");
        film.setDuration(120L);

        Long comedyId = jdbcTemplate.queryForObject(
                "SELECT genre_id FROM genres WHERE name = 'Комедия'", Long.class);
        Long dramaId = jdbcTemplate.queryForObject(
                "SELECT genre_id FROM genres WHERE name = 'Драма'", Long.class);

        Genre genre1 = new Genre();
        genre1.setGenreId(comedyId);
        genre1.setName("Комедия");

        Genre genre2 = new Genre();
        genre2.setGenreId(dramaId);
        genre2.setName("Драма");

        film.setGenres(new HashSet<>(Arrays.asList(genre1, genre2)));

        Integer ratingId = jdbcTemplate.queryForObject(
                "SELECT rating_id FROM rating WHERE name = 'G'", Integer.class);

        Mpa mpa = new Mpa();
        mpa.setRatingId(ratingId);
        mpa.setName("G");
        film.setMpa(mpa);

        user = new User();
        user.setEmail("JohnSnow" + System.currentTimeMillis() + "@mail.ru");
        user.setLogin("JohnSnow");
        user.setName("JohnSnow");
        user.setBirthday("2000-01-01");
    }

    @Test
    void createMovieWithEmptyNameWillResultErrorBadRequest() throws Exception {
        film.setName("");
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages.name")
                        .value("Название фильма не может быть пустым"));
    }

    @Test
    void createMovieWithEmptyDescriptionWillResultErrorBadRequest() throws Exception {
        film.setDescription("");
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages.description")
                        .value("Описание фильма не может быть пустым"));
    }

    @Test
    void createMovieWithDescriptionOfMoreThan200CharactersWillResultErrorBadRequest() throws Exception {
        film.setDescription("a".repeat(201));
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages.description")
                        .value("Описание фильма должно включать не более 200 символов"));
    }

    @Test
    void createMovieWithEmptyReleaseDateLeadsToErrorBadRequest() throws Exception {
        film.setReleaseDate(null);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages.releaseDate")
                        .value("Дата релиза не может быть null"));
    }

    @Test
    void createMovieWithReleaseDateWrongFormatLeadsToErrorBadRequest() throws Exception {
        film.setReleaseDate("2001.01.01");
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages.releaseDate")
                        .value("Дата релиза должна быть в формате yyyy-MM-dd"));
    }

    @Test
    void createMovieWithNegativeDurationLeadsToErrorBadRequest() throws Exception {
        film.setDuration(-120L);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages.duration")
                        .value("продолжительность фильма не может быть отрицательной или равной нулю"));
    }

    @Test
    void createValidFilmIsSuccessful() throws Exception {
        Integer ratingId = jdbcTemplate.queryForObject(
                "SELECT rating_id FROM rating WHERE name = 'G'", Integer.class);

        Mpa mpa = new Mpa();
        mpa.setRatingId(ratingId);
        mpa.setName("G");
        film.setMpa(mpa);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Film"))
                .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    void updateNonExistentFilmShouldFailNotFound() throws Exception {
        film.setId(10L);
        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Фильм с id = 10 не найден"));
    }

    @Test
    void updateMovieWithFoundIdIsSuccessful() throws Exception {
        String response = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Integer createdId = JsonPath.read(response, "$.id");
        film.setId(createdId.longValue());
        film.setName("FilmUpdate");
        film.setDescription("DescriptionUpdate");
        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("FilmUpdate"))
                .andExpect(jsonPath("$.description").value("DescriptionUpdate"));
    }

    @Test
    void addLikeFromNonExistentUserWillResultErrorNotFoundException() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.put("/films/1/like/99999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Пользователь с id = 99999999 не найден"));
    }

    @Test
    void addLikeToExistingFilmFromValidUserIsSuccessful() throws Exception {
        String filmResponse = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long filmId = objectMapper.readTree(filmResponse).path("id").asLong();
        user.setEmail("JohnSnow" + System.currentTimeMillis() + "@mail.ru");
        String userResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long userId = objectMapper.readTree(userResponse).path("id").asLong();
        mockMvc.perform(MockMvcRequestBuilders.put("/films/" + filmId + "/like/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    void deleteLikeFromExistingFilmByValidUserIsSuccessful() throws Exception {
        String filmResponse = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long filmId = objectMapper.readTree(filmResponse).path("id").asLong();
        user.setEmail("JohnSnow" + System.currentTimeMillis() + "@mail.ru");
        String userResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long userId = objectMapper.readTree(userResponse).path("id").asLong();
        mockMvc.perform(MockMvcRequestBuilders.put("/films/" + filmId + "/like/" + userId))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/films/" + filmId + "/like/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    void getFilmWithGenresReturnsCorrectGenres() throws Exception {
        String filmResponse = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long filmId = objectMapper.readTree(filmResponse).path("id").asLong();
        Long comedyId = jdbcTemplate.queryForObject(
                "SELECT genre_id FROM genres WHERE name = 'Комедия'", Long.class);
        mockMvc.perform(get("/films/{id}", filmId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genres[0].id").value(comedyId.intValue())) // Проверяем, что id = 1
                .andExpect(jsonPath("$.genres[0].name").value("Комедия"));
    }
}

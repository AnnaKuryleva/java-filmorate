package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.model.Film;

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
    private ObjectMapper objectMapper;
    private Film film;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setId(1L);
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate("2001-01-01");
        film.setDuration(120);
    }

    @Test
    void createMovieWithEmptyNameWillResultErrorBadRequest() throws Exception {
        film.setName("");
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages.name")
                        .value("Название фильма не может быть пустым"));
    }

    @Test
    void createMovieWithEmptyDescriptionWillResultErrorBadRequest() throws Exception {
        film.setDescription("");
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages.description")
                        .value("Описание фильма не может быть пустым"));
    }

    @Test
    void createMovieWithDescriptionOfMoreThan200CharactersWillResultErrorBadRequest() throws Exception {
        film.setDescription("a".repeat(201));
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages.description")
                        .value("Описание фильма должно включать не более 200 символов"));
    }

    @Test
    void createMovieWithEmptyReleaseDateLeadsToErrorBadRequest() throws Exception {
        film.setReleaseDate(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages.releaseDate")
                        .value("Дата релиза не может быть null"));
    }

    @Test
    void createMovieWithReleaseDateWrongFormatLeadsToErrorBadRequest() throws Exception {
        film.setReleaseDate("2001.01.01");
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages.releaseDate")
                        .value("Дата релиза должна быть в формате yyyy-MM-dd"));
    }

    @Test
    void createMovieWithNegativeDurationLeadsToErrorBadRequest() throws Exception {
        film.setDuration(-120);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages.duration")
                        .value("продолжительность фильма не может быть отрицательной или равной нулю"));
    }

    @Test
    void createValidFilmIsSuccessful() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Film"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Description"));
    }

    @Test
    void updateNonExistentFilmShouldFailNotFound() throws Exception {
        film.setId(10L);
        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Пост с id = 10 не найден"));
    }

    @Test
    void updateMovieWithFoundIdIsSuccessful() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        film.setId(1L);
        film.setName("FilmUpdate");
        film.setDescription("DescriptionUpdate");

        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("FilmUpdate"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("DescriptionUpdate"));
    }
}

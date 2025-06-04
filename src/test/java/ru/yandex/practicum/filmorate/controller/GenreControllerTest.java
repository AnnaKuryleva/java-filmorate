package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    private Genre genre1;
    private Genre genre2;

    @BeforeEach
    void setUp() {
        genre1 = new Genre();
        genre1.setGenreId(1L);
        genre1.setName("Комедия");

        genre2 = new Genre();
        genre2.setGenreId(2L);
        genre2.setName("Драма");
    }

    @Test
    void creatingValidGenresWillSuccessfullyReturnTheirList() throws Exception {
        Collection<Genre> genres = Arrays.asList(genre1, genre2);
        when(genreService.findAll()).thenReturn(genres);
        mockMvc.perform(get("/genres")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Комедия"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Драма"));
        verify(genreService, times(1)).findAll();
    }

    @Test
    void searchForValidIdReturnsGenreSuccessfullyOtherwiseItThrowsException() throws Exception {
        when(genreService.findById(1L)).thenReturn(genre1);
        when(genreService.findById(999L)).thenThrow(new RuntimeException("Жанр с id=999 не найден"));
        mockMvc.perform(get("/genres/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Комедия"));
        mockMvc.perform(get("/genres/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
        verify(genreService, times(1)).findById(1L);
        verify(genreService, times(1)).findById(999L);
    }
}

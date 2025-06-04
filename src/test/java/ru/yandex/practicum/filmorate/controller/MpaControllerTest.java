package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MpaController.class)
class MpaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MpaService mpaService;

    private Mpa mpa1;
    private Mpa mpa2;

    @BeforeEach
    void setUp() {
        mpa1 = new Mpa();
        mpa1.setRatingId(1);
        mpa1.setName("G");


        mpa2 = new Mpa();
        mpa2.setRatingId(2);
        mpa2.setName("RG");
    }

    @Test
    void getAllMpaRatingsReturnsListOfRatings() throws Exception {
        Collection<Mpa> ratings = Arrays.asList(mpa1, mpa2);
        when(mpaService.getAllMpaRatings()).thenReturn(ratings);
        mockMvc.perform(get("/mpa")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("G"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("RG"));
        verify(mpaService, times(1)).getAllMpaRatings();
    }

    @Test
    void gettingRatingOnlyBasedOnExistingIdReturnsSuccessfulStatus() throws Exception {
        when(mpaService.getMpaRatingById(1)).thenReturn(Optional.of(mpa1));
        when(mpaService.getMpaRatingById(999)).thenReturn(Optional.empty());
        mockMvc.perform(get("/mpa/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("G"));
        mockMvc.perform(get("/mpa/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(mpaService, times(1)).getMpaRatingById(1);
        verify(mpaService, times(1)).getMpaRatingById(999);
    }
}

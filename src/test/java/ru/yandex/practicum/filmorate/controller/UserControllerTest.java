package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.yandex.practicum.filmorate.model.User;

import java.util.UUID;

/**
 * Тестовый класс для проверки функциональности {@link UserController}.
 * Класс содержит тесты для проверки операций с пользователями,
 * таких как создание, обновление и получение списка пользователей, с использованием Spring Boot и MockMvc.
 */

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setLogin("login");
        user.setEmail("Bob" + UUID.randomUUID() + "@example.com");
        user.setName("Name");
        user.setBirthday("1990-01-01");
    }

    @Test
    void createUserWithInvalidEmailAddressWillBadRequest() throws Exception {
        user.setEmail("invalidEmail");
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages.email")
                        .value("поле должно иметь формат адреса электронной почты"));
    }

    @Test
    void createUserWithEmptyEmailAddressWillBadRequest() throws Exception {
        user.setEmail(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages.email")
                        .value("электронная почта не может быть null"));
    }

    @Test
    void createValidUserIsSuccessful() throws Exception {
        String uniqueEmail = "Jon" + System.currentTimeMillis() + "@example.com";
        user.setEmail(uniqueEmail);
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value("login"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(uniqueEmail));
    }

    @Test
    void createUserWithEmptyNameShouldSetLoginAsName() throws Exception {
        user.setName("");
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("login"));
    }

    @Test
    void createUserWithNameShouldSetLoginAsName() throws Exception {
        user.setName(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("login"));
    }

    @Test
    void updateUserWithFoundIdIsSuccessful() throws Exception {
        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM friendship");
        jdbcTemplate.update("DELETE FROM users");
        user.setEmail("Bob" + UUID.randomUUID() + "@example.com");
        String createResponse = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long userId = objectMapper.readTree(createResponse).get("id").asLong();
        user.setId(userId);
        user.setLogin("loginUpdate");
        user.setEmail("JonUpdate@example.com");
        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value("loginUpdate"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("JonUpdate@example.com"));
    }

    @Test
    void updateNonExistentUserShouldFailNotFound() throws Exception {
        user.setId(10L);
        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Пользователь с id = 10 не найден"));
    }

    @Test
    void getAllUsersReturnsEmptyArrayIfThereAreNoUsers() throws Exception {
        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM friendship");
        jdbcTemplate.update("DELETE FROM users");
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    void createUserWithUsernameContainingSpacesWillReturnBadRequest() throws Exception {
        user.setLogin("");
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages.login")
                        .value("Логин не может содержать пробелы"));
    }

    @Test
    void createUserWithEmptyUsernameWillReturnBadRequest() throws Exception {
        user.setLogin(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages.login")
                        .value("Логин не может быть пустым"));
    }

    @Test
    void createUserWithDateOfBirthInFutureWillReturnBadRequest() throws Exception {
        user.setBirthday("3990-01-01");
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages.birthday")
                        .value("Дата рождения не может быть в будущем"));
    }

    @Test
    void unilateralAdditionOfFriendshipHasStatusOfSuccessfulOnPartOfApplicant() throws Exception {
        user.setEmail("Bob" + System.currentTimeMillis() + "@example.com");
        String user1Response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long user1Id = objectMapper.readTree(user1Response).get("id").asLong();
        user.setEmail("Alice" + System.currentTimeMillis() + "@example.com");
        String user2Response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long user2Id = objectMapper.readTree(user2Response).get("id").asLong();
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + user1Id + "/friends/" + user2Id))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + user1Id + "/friends"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(user2Id));
        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + user2Id + "/friends"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    void getCommonFriendsIsSuccessful() throws Exception {
        user.setEmail("Bob" + System.currentTimeMillis() + "@example.com");
        String user1Response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long user1Id = objectMapper.readTree(user1Response).get("id").asLong();
        user.setEmail("Alice" + System.currentTimeMillis() + "@example.com");
        String user2Response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long user2Id = objectMapper.readTree(user2Response).get("id").asLong();
        user.setEmail("Charlie" + System.currentTimeMillis() + "@example.com");
        String user3Response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long user3Id = objectMapper.readTree(user3Response).get("id").asLong();
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + user1Id + "/friends/" + user3Id))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + user2Id + "/friends/" + user3Id))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + user1Id + "/friends/common/" + user2Id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(user3Id));
    }
}

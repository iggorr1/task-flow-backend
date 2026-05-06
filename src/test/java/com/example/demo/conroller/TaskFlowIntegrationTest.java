package com.example.demo.conroller;

import com.example.demo.DemoApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
class TaskFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldRegisterLoginCreateTaskUpdateStatusAndFilterByStatus() throws Exception {
        String unique = String.valueOf(System.currentTimeMillis());

        String login = "test_user_" + unique;
        String email = "test_" + unique + "@test.com";
        String password = "123456";

        // 1. Register user
        String registerJson = """
                {
                  "name": "Test User",
                  "email": "%s",
                  "login": "%s",
                  "password": "%s"
                }
                """.formatted(email, login, password);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(login))
                .andExpect(jsonPath("$.email").value(email));

        // 2. Login and get JWT token
        String loginJson = """
                {
                  "login": "%s",
                  "password": "%s"
                }
                """.formatted(login, password);

        String loginResponse = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode loginJsonNode = objectMapper.readTree(loginResponse);
        String token = loginJsonNode.get("token").asText();

        // 3. Create task
        String taskJson = """
                {
                  "title": "Integration Test Task",
                  "description": "Created from integration test"
                }
                """;

        String taskResponse = mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Test Task"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.completed").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode taskJsonNode = objectMapper.readTree(taskResponse);
        Long taskId = taskJsonNode.get("id").asLong();

        // 4. Update task status to DONE
        String updateStatusJson = """
                {
                  "status": "DONE"
                }
                """;

        mockMvc.perform(patch("/tasks/" + taskId + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateStatusJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.completed").value(true));

        // 5. Filter tasks by status DONE
        mockMvc.perform(get("/tasks")
                        .param("status", "DONE")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", hasItem(taskId.intValue())))
                .andExpect(jsonPath("$.content[*].status", hasItem("DONE")));
    }
}
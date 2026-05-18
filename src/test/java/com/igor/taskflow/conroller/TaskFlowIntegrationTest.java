package com.igor.taskflow.conroller;

import com.igor.taskflow.TaskFlowApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TaskFlowApplication.class)
@AutoConfigureMockMvc
class TaskFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldRegisterLoginCreateTaskUpdateStatusAndFilterByStatus() throws Exception {
        TestUser user = registerAndLogin("test_user");

        Long taskId = createTask(user.token(), "Integration Test Task");

        String updateStatusJson = """
                {
                  "status": "DONE"
                }
                """;

        mockMvc.perform(patch("/tasks/" + taskId + "/status")
                        .header("Authorization", "Bearer " + user.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateStatusJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.completed").value(true));

        mockMvc.perform(get("/tasks")
                        .param("status", "DONE")
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", hasItem(taskId.intValue())))
                .andExpect(jsonPath("$.content[*].status", hasItem("DONE")));
    }

    @Test
    void shouldRejectTasksRequestWithoutToken() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectAdminEndpointForRegularUser() throws Exception {
        TestUser user = registerAndLogin("regular_user");

        mockMvc.perform(get("/admin/stats")
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldPreventUserFromReadingAnotherUsersTask() throws Exception {
        TestUser owner = registerAndLogin("owner_user");
        TestUser otherUser = registerAndLogin("other_user");
        Long ownerTaskId = createTask(owner.token(), "Private task");

        mockMvc.perform(get("/tasks/" + ownerTaskId)
                        .header("Authorization", "Bearer " + otherUser.token()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied to this task"));
    }

    @Test
    void shouldPreventUserFromDeletingAnotherUsersTask() throws Exception {
        TestUser owner = registerAndLogin("delete_owner");
        TestUser otherUser = registerAndLogin("delete_other");
        Long ownerTaskId = createTask(owner.token(), "Do not delete");

        mockMvc.perform(delete("/tasks/" + ownerTaskId)
                        .header("Authorization", "Bearer " + otherUser.token()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied to this task"));

        mockMvc.perform(get("/tasks/" + ownerTaskId)
                        .header("Authorization", "Bearer " + owner.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ownerTaskId));
    }

    @Test
    void shouldRejectDuplicateLoginAndEmail() throws Exception {
        String unique = uniqueSuffix();
        String login = "duplicate_user_" + unique;
        String email = "duplicate_" + unique + "@test.com";

        String registerJson = registerJson(login, email);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson(login, "another_" + email)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Login already exists"));

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson("another_" + login, email)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    private TestUser registerAndLogin(String loginPrefix) throws Exception {
        String unique = uniqueSuffix();
        String login = loginPrefix + "_" + unique;
        String email = loginPrefix + "_" + unique + "@test.com";
        String password = "123456";

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson(login, email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(login))
                .andExpect(jsonPath("$.email").value(email));

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
        return new TestUser(login, loginJsonNode.get("token").asText());
    }

    private Long createTask(String token, String title) throws Exception {
        String taskJson = """
                {
                  "title": "%s",
                  "description": "Created from integration test"
                }
                """.formatted(title);

        String taskResponse = mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.completed").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode taskJsonNode = objectMapper.readTree(taskResponse);
        return taskJsonNode.get("id").asLong();
    }

    private String registerJson(String login, String email) {
        return registerJson(login, email, "123456");
    }

    private String registerJson(String login, String email, String password) {
        return """
                {
                  "name": "Test User",
                  "email": "%s",
                  "login": "%s",
                  "password": "%s"
                }
                """.formatted(email, login, password);
    }

    private String uniqueSuffix() {
        return String.valueOf(System.nanoTime());
    }

    private record TestUser(String login, String token) {
    }
}

package com.taskflow.securetaskflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflow.securetaskflow.auth.RegisterRequest;
import com.taskflow.securetaskflow.project.CreateProjectRequest;
import com.taskflow.securetaskflow.task.ChangeTaskStatusRequest;
import com.taskflow.securetaskflow.task.CreateCommentRequest;
import com.taskflow.securetaskflow.task.CreateTaskRequest;
import com.taskflow.securetaskflow.task.TaskPriority;
import com.taskflow.securetaskflow.task.TaskStatus;
import com.taskflow.securetaskflow.team.CreateTeamRequest;
import com.taskflow.securetaskflow.user.Role;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void managerCanCreateTeamProjectTaskAndMoveTaskThroughWorkflow() throws Exception {
        String token = register("manager@example.com", Role.MANAGER);

        Long teamId = createTeam(token);
        Long projectId = createProject(token, teamId);
        Long taskId = createTask(token, projectId);

        mockMvc.perform(patch("/api/tasks/{taskId}/status", taskId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChangeTaskStatusRequest(TaskStatus.IN_PROGRESS))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo("IN_PROGRESS")));

        mockMvc.perform(post("/api/tasks/{taskId}/comments", taskId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateCommentRequest("Initial API contract is ready."))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/tasks/{taskId}/comments", taskId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].body", equalTo("Initial API contract is ready.")));
    }

    private String register(String email, Role role) throws Exception {
        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest(
                                "TaskFlow User",
                                email,
                                "password123",
                                role
                        ))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    private Long createTeam(String token) throws Exception {
        String response = mockMvc.perform(post("/api/teams")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateTeamRequest(
                                "Platform Team",
                                "Owns backend workflow services"
                        ))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return idFrom(response);
    }

    private Long createProject(String token, Long teamId) throws Exception {
        String response = mockMvc.perform(post("/api/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateProjectRequest(
                                teamId,
                                "TaskFlow API Launch",
                                "Prepare the first public backend release.",
                                LocalDate.now().plusDays(30)
                        ))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return idFrom(response);
    }

    private Long createTask(String token, Long projectId) throws Exception {
        String response = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateTaskRequest(
                                projectId,
                                "Design project endpoints",
                                "Define project create/list/update flows.",
                                TaskPriority.HIGH,
                                LocalDate.now().plusDays(7),
                                null
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", equalTo("TODO")))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return idFrom(response);
    }

    private Long idFrom(String response) throws Exception {
        JsonNode json = objectMapper.readTree(response);
        return json.get("id").asLong();
    }
}

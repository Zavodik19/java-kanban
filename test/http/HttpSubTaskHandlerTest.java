package http;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.SubTask;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpSubTaskHandlerTest {

    private final TaskManager manager;
    private final HttpTaskServer taskServer;
    private final Gson gson;

    private static final String SUBTASKS_URL = "http://localhost:8080/subtasks";

    public HttpSubTaskHandlerTest() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTask();
        manager.deleteAllSubTask();
        manager.deleteAllEpic();
        taskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Epic description",
                TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        manager.addTask(epic);

        SubTask subTask = new SubTask("Test SubTask", "SubTask description",
                TaskStatus.NEW, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        String subTaskJson = gson.toJson(subTask);

        // Отправка POST-запроса
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SUBTASKS_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<SubTask> subTasksFromManager = manager.getAllSubTasks();
        assertNotNull(subTasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subTasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test SubTask", subTasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Epic description",
                TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        manager.addTask(epic);

        SubTask subTask1 = new SubTask("Test SubTask 1", "SubTask description",
                TaskStatus.NEW, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now().plusHours(2));
        manager.addSubTask(subTask1);

        SubTask subTask2 = new SubTask("Test SubTask 2", "SubTask description",
                TaskStatus.NEW, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now().plusHours(3));
        manager.addSubTask(subTask2);

        // Отправка GET-запроса
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(SUBTASKS_URL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Epic description",
                TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        manager.addTask(epic);

        SubTask subTask = new SubTask("Test SubTask", "SubTask description",
                TaskStatus.NEW, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        manager.addSubTask(subTask);

        // Отправка DELETE-запроса
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(SUBTASKS_URL + "/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        List<SubTask> subTasksFromManager = manager.getAllSubTasks();
        assertEquals(0, subTasksFromManager.size(), "Подзадача не была удалена");
    }
}
package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import static http.HttpEpicHandlerTest.EPICS_URL;
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
        URI url = URI.create(SUBTASKS_URL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
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
        // Создание эпика
        Epic epic = new Epic("Test Epic", "Epic description",
                TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        manager.addTask(epic);
        String epicJson = gson.toJson(epic);

        // Отправка POST-запроса для создания эпика
        HttpClient client = HttpClient.newHttpClient();
        URI epicUrl = URI.create(EPICS_URL);
        HttpRequest epicRequest = HttpRequest.newBuilder()
                .uri(epicUrl)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, epicResponse.statusCode()); // Ожидаем статус 201 Created

        // Создание подзадачи, которая не пересекается по времени
        SubTask subTask1 = new SubTask("Test SubTask 1", "SubTask description",
                TaskStatus.NEW, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        String subTask1Json = gson.toJson(subTask1);

        // Отправка POST-запроса для создания подзадачи
        URI subTaskUrl = URI.create(SUBTASKS_URL);
        HttpRequest subTaskRequest1 = HttpRequest.newBuilder()
                .uri(subTaskUrl)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subTask1Json))
                .build();

        HttpResponse<String> subTaskResponse1 = client.send(subTaskRequest1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, subTaskResponse1.statusCode()); // Ожидаем статус 201 Created

        // Создание подзадачи, которая не пересекается по времени
        SubTask subTask2 = new SubTask("Test SubTask 2", "SubTask description",
                TaskStatus.NEW, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(40));
        String subTask2Json = gson.toJson(subTask2);

        // Отправка POST-запроса для создания подзадачи
        HttpRequest subTaskRequest2 = HttpRequest.newBuilder()
                .uri(subTaskUrl)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subTask2Json))
                .build();

        HttpResponse<String> subTaskResponse2 = client.send(subTaskRequest2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, subTaskResponse2.statusCode()); // Ожидаем статус 201 Created

        // Получение подзадач
        HttpRequest getSubTasksRequest = HttpRequest.newBuilder()
                .uri(URI.create(SUBTASKS_URL + "?epicId=" + epic.getId()))
                .GET()
                .build();

        HttpResponse<String> getSubTasksResponse = client.send(getSubTasksRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getSubTasksResponse.statusCode()); // Ожидаем статус 200 OK

        // Проверка, что количество подзадач равно 2
        List<SubTask> subTasks = gson.fromJson(getSubTasksResponse.body(), new TypeToken<List<SubTask>>() {
        }.getType());
        assertEquals(2, subTasks.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {
        // Создание эпика
        Epic epic = new Epic("Test Epic", "Epic description",
                TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        String epicJson = gson.toJson(epic);

        // Отправка POST-запроса для создания эпика
        HttpClient client = HttpClient.newHttpClient();
        URI epicUrl = URI.create(EPICS_URL);
        HttpRequest epicRequest = HttpRequest.newBuilder()
                .uri(epicUrl)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, epicResponse.statusCode()); // Ожидаем статус 201 Created

        // Создание подзадачи
        SubTask subTask = new SubTask("Test SubTask", "SubTask description",
                TaskStatus.NEW, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        String subTaskJson = gson.toJson(subTask);

        // Отправка POST-запроса для создания подзадачи
        URI subTaskUrl = URI.create(SUBTASKS_URL);
        HttpRequest subTaskRequest = HttpRequest.newBuilder()
                .uri(subTaskUrl)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> subTaskResponse = client.send(subTaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, subTaskResponse.statusCode()); // Ожидаем статус 201 Created

        // Удаление подзадачи
        URI deleteSubTaskUrl = URI.create(SUBTASKS_URL + "/" + subTask.getId());
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(deleteSubTaskUrl)
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, deleteResponse.statusCode()); // Ожидаем статус 204 No Content

        // Проверка, что подзадача удалена
        HttpRequest getSubTasksRequest = HttpRequest.newBuilder()
                .uri(URI.create(SUBTASKS_URL + "?epicId=" + epic.getId()))
                .GET()
                .build();

        HttpResponse<String> getSubTasksResponse = client.send(getSubTasksRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getSubTasksResponse.statusCode()); // Ожидаем статус 200 OK

        // Проверка, что количество подзадач равно 0
        List<SubTask> subTasks = gson.fromJson(getSubTasksResponse.body(), new TypeToken<List<SubTask>>() {
        }.getType());
        assertEquals(0, subTasks.size(), "Подзадача не была удалена");
    }
}

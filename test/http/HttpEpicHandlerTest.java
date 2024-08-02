package http;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
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

public class HttpEpicHandlerTest {

    private final TaskManager manager;
    private final HttpTaskServer taskServer;
    private final Gson gson;

    private static final String EPICS_URL = "http://localhost:8080/epics";

    public HttpEpicHandlerTest() throws IOException {
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
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Epic description",
                TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        String epicJson = gson.toJson(epic);

        // Отправка POST-запроса
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPICS_URL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        // Проверка добавленного эпика
        List<Epic> epicsFromManager = manager.getAllEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test Epic", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Epic description",
                TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        manager.addTask(epic);

        // Отправка GET-запроса
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPICS_URL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Epic description",
                TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        manager.addTask(epic);

        // Отправка DELETE-запроса
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPICS_URL + "/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpics();
        assertEquals(0, epicsFromManager.size(), "Эпик не был удален");
    }
}
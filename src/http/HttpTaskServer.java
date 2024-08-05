package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import http.adapter.DurationAdapter;
import http.adapter.LocalDateTimeAdapter;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static HttpServer httpServer;
    private final TaskManager taskManager;
    private static final Gson gson = Managers.createGson(); // Вызов метода создания Gson из нового местоположения

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }


    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
        httpTaskServer.startServer();
    }

    public static Gson getGson() {
        return gson;
    }

    public void startServer() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
            // Привязка обработчиков к путям
            httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
            httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
            httpServer.createContext("/subtasks", new SubTaskHandler(taskManager, gson));
            httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
            httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
            httpServer.start();
            System.out.println("Сервер запущен на порту " + 8080);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось запустить сервер", e);
        }
    }

    public void stopServer() {
        if (httpServer != null) {
            httpServer.stop(0);
            System.out.println("Сервер остановлен");
        }
    }
}
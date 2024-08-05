package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.TaskCrossingTimeException;
import manager.TaskManager;
import model.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGetTasks(exchange);
                break;
            case "POST":
                handleAddTask(exchange);
                break;
            case "DELETE":
                handleDeleteTask(exchange);
                break;
            default:
                sendText(exchange, "Метод не поддерживается", 204);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        String response = gson.toJson(tasks);
        sendText(exchange, response, 200);
    }

    private void handleAddTask(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        Task task = gson.fromJson(reader, Task.class);
        try {
            Task createdTask = taskManager.addTask(task);
            String jsonResponse = gson.toJson(createdTask);
            sendText(exchange, jsonResponse, 201);
        } catch (TaskCrossingTimeException e) {
            sendHasInteractions(exchange, "Задача пересекается по времени с другими.");
        } catch (Exception e) {
            sendNotFound(exchange, "Ошибка при добавлении задачи.");
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
        taskManager.deleteTask(id);
        sendText(exchange, "Задача удалена", 204);
    }
}
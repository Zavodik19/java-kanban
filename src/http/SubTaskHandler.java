package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.TaskCrossingTimeException;
import manager.TaskManager;
import model.SubTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubTaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGetSubTasks(exchange);
                break;
            case "POST":
                handleAddSubTask(exchange);
                break;
            case "DELETE":
                handleDeleteSubTask(exchange);
                break;
            default:
                sendNotFound(exchange, "Метод не поддерживается");
        }
    }

    private void handleGetSubTasks(HttpExchange exchange) throws IOException {
        List<SubTask> subTasks = taskManager.getAllSubTasks();
        String response = gson.toJson(subTasks);
        sendText(exchange, response, 200);
    }

    private void handleAddSubTask(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        SubTask subTask = gson.fromJson(reader, SubTask.class);

        if (subTask == null) {
            sendNotFound(exchange, "Подзадача не может быть null.");
            return;
        }

        try {
            SubTask createdSubTask = taskManager.addSubTask(subTask);
            String jsonResponse = gson.toJson(createdSubTask);
            sendCreated(exchange, jsonResponse);
        } catch (TaskCrossingTimeException e) {
            sendHasInteractions(exchange, "Подзадача пересекается по времени с другими.");
        } catch (Exception e) {
            sendNotFound(exchange, "Ошибка при добавлении подзадачи: " + e.getMessage());
        }
    }

    private void handleDeleteSubTask(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
        taskManager.deleteSubTask(id);
        sendText(exchange, "Подзадача удалена", 204);
    }
}
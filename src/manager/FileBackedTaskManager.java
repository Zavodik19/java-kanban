package manager;

import exception.ManagerSaveException;
import exception.TaskManagerLoadException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File createFile = new File("resources/file.csv"); // Добавлен private final
    private final File file;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public void save() {
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic,duration,startTime\n");
            List<Task> taskList = new ArrayList<>();
            taskList.addAll(tasks.values());
            taskList.addAll(epics.values());
            taskList.addAll(subtasks.values());
            for (Task task : taskList) {
                String line = taskToString(task) + "\n";
                writer.append(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }
    }


    private String taskToString(Task task) {
        String line = task.getId() + "," + task.getTaskType().name() + "," + task.getName() + "," +
                task.getStatus() + "," + task.getDescription() + "," +
                (task instanceof SubTask ? ((SubTask) task).getEpicId() : "") + "," +
                (task.getDuration() != null ? task.getDuration().toMinutes() : "") + "," +
                (task.getStartTime() != null ? task.getStartTime().format(FORMATTER) : "");
        return line;
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    Task task = fromString(line);
                    manager.addTask(task);
                }
            }
        } catch (IOException e) {  // Добавлен проброс собственного исключения
            throw new TaskManagerLoadException("Ошибка при загрузке задач из файла", e);
        }
        return manager;
    }


    public static Task fromString(String value) {
        String[] parts = value.split(",\\s*");
        if (parts.length < 7) {
            throw new IllegalArgumentException("Недостаточно данных для создания задачи");
        }
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2].trim();
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4].trim();
        Duration duration = parts[6].isEmpty() ? null : Duration.ofMinutes(Long.parseLong(parts[6]));
        LocalDateTime startTime = parts[7].isEmpty() ? null : LocalDateTime.parse(parts[7], FORMATTER);

        switch (type) {
            case TASK:
                return new Task(name, description, status, duration, startTime);
            case EPIC:
                Epic epic = new Epic(name, description, status, duration, startTime);
                epic.setDuration(duration);
                epic.setStartTime(startTime);
                return epic;
            case SUBTASK:
                if (parts.length < 8) {
                    throw new IllegalArgumentException("Недостаточно данных для создания подзадачи");
                }
                int epicId = Integer.parseInt(parts[5]);
                SubTask subTask = new SubTask(name, description, status, epicId, duration, startTime);
                return subTask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }


    @Override
    public Task addTask(Task task) {
        Task task1 = super.addTask(task);
        save();
        return task1;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        SubTask subTask1 = super.addSubTask(subTask);
        save();
        return subTask1;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic epic1 = super.addEpic(epic);
        save();
        return epic1;
    }


    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpicStatus(int epicId) {
        super.updateEpicStatus(epicId);
        save();
    }


    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

}

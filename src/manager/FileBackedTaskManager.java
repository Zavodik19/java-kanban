package manager;

import exception.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static model.TaskType.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    File createFile = new File("resources/file.csv");
    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }


    public void save() throws ManagerSaveException {
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic \n"); // заголовки таблицы
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
        String taskType;
        String line = "";
        if (Task.class.equals(task.getClass())) {
            taskType = TASK.name();
            line = task.getId() + ", " + taskType + ", " + task.getName() + ", " + task.getStatus() + ", " +
                    task.getDescription();
        } else if (Epic.class.equals(task.getClass())) {
            taskType = EPIC.name();
            line = task.getId() + ", " + taskType + ", " + task.getName() + ", " + task.getStatus() + ", " +
                    task.getDescription();
        } else if (SubTask.class.equals(task.getClass())) {
            taskType = SUBTASK.name();
            line = task.getId() + ", " + taskType + ", " + task.getName() + ", " + task.getStatus() + ", " +
                    task.getDescription() + ", " + ((SubTask) task).getEpicId();
        }
        return line;
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file); // Исправлено: передаем файл в конструктор
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // Пропускаем строку с заголовками
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) { // Проверяем, не пустая ли строка
                    Task task = fromString(line); // Используем статический метод fromString
                    manager.addTask(task); // Добавляем задачу в менеджер
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return manager;
    }


    public static Task fromString(String value) {
        String[] parts = value.split(",\\s*"); // Разделяем строку по запятой и пробелам
        if (parts.length < 5) {
            throw new IllegalArgumentException("Недостаточно данных для создания задачи");
        }
        int id = Integer.parseInt(parts[0]); // Получаем ID задачи
        TaskType type = TaskType.valueOf(parts[1]); // Получаем тип задачи
        String name = parts[2].trim(); // Получаем имя задачи
        TaskStatus status = TaskStatus.valueOf(parts[3]); // Получаем статус задачи
        String description = parts[4].trim(); // Получаем описание задачи

        switch (type) {
            case TASK:
                return new Task(name, description, status);
            case EPIC:
                return new Epic(name, description);
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]); // Получаем ID эпика для подзадачи
                return new SubTask(name, description, status, epicId);
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

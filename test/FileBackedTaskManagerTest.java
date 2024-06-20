import manager.FileBackedTaskManager;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private final File file = new File("resources/test.csv");
    private FileBackedTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new FileBackedTaskManager(file);
    }


    @Test
    public void testSaveAndLoadTasks() throws IOException {
        // Создаем временный файл
        File tempFile = File.createTempFile("test", ".csv");

        // Инициализируем менеджер с временным файлом
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task task = new Task("Test", "Description", TaskStatus.NEW);
        manager.addTask(task);

        // Сохраняем задачи в файл
        manager.save();

        // Загружаем задачи из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loadedManager.getTask(task.getId());

        // Проверяем, что задача загрузилась корректно
        assertEquals(task, loadedTask);

        // Удаляем временный файл
        assertTrue(tempFile.delete());
    }


    // Тестирование добавления задачи
    @Test
    void shouldAddTaskCorrectly() {
        Task task = new Task("Test Task", "Description", TaskStatus.NEW);
        taskManager.addTask(task);
        assertFalse(taskManager.getAllTasks().isEmpty());
        assertEquals(1, taskManager.getAllTasks().size());
    }

    // Тестирование удаления задачи
    @Test
    void shouldRemoveTaskCorrectly() {
        Task task = new Task("Test Task", "Description", TaskStatus.NEW);
        taskManager.addTask(task);
        int taskId = task.getId();
        taskManager.deleteTask(taskId);
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    // Тестирование получения списка всех задач
    @Test
    void shouldGetAllTasks() {
        Task task1 = new Task("Test Task 1", "Description", TaskStatus.NEW);
        Task task2 = new Task("Test Task 2", "Description", TaskStatus.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size());
    }

    // Тестирование сохранения состояния в файл
    @Test
    void shouldSaveStateToFile() {
        Task task = new Task("Test Task", "Description", TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.save();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    // Тестирование загрузки состояния из файла
    @Test
    void shouldLoadStateFromFile() {
        Task task = new Task("Test Task", "Description", TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(taskManager.getAllTasks(), loadedManager.getAllTasks());
    }

}
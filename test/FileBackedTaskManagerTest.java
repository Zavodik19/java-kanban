import manager.FileBackedTaskManager;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @Override
    @BeforeEach
    public void setUp() {
        try {
            file = File.createTempFile("test", ".txt");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Не удалось создать временный файл для тестов.");
        }
        taskManager = new FileBackedTaskManager(file);
    }


    @Test
    void shouldSaveAndLoadTasks() {
        LocalDateTime fixedTime = LocalDateTime.of(2024, 6, 27, 10, 0);
        Task task = new Task("Test Task", "Description", TaskStatus.NEW,
                Duration.ofHours(1), fixedTime);
        taskManager.addTask(task);
        taskManager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Task loadedTask = loadedManager.getTask(task.getId());

        assertEquals(task.getName(), loadedTask.getName());
        assertEquals(task.getDescription(), loadedTask.getDescription());
        assertEquals(task.getStatus(), loadedTask.getStatus());
        assertEquals(task.getDuration(), loadedTask.getDuration());
        assertEquals(fixedTime, loadedTask.getStartTime());
    }

    @Test
    void shouldCorrectlyAddTask() {
        LocalDateTime fixedTime = LocalDateTime.of(2024, 6, 27, 10, 0);
        Task task = new Task("New Task", "Description", TaskStatus.NEW,
                Duration.ofHours(1), fixedTime);
        taskManager.addTask(task);
        Task retrievedTask = taskManager.getTask(task.getId());
        assertNotNull(retrievedTask);
        assertEquals("New Task", retrievedTask.getName());
    }

    @Test
    void shouldCorrectlyUpdateTask() {
        LocalDateTime fixedTime = LocalDateTime.of(2024, 6, 27, 10, 0);
        Task task = new Task("Update Task", "Description", TaskStatus.NEW,
                Duration.ofHours(1), fixedTime);
        int taskId = taskManager.addTask(task).getId();
        Task updatedTask = new Task("Updated Task", "Updated Description", TaskStatus.DONE,
                Duration.ofHours(2), fixedTime.plusDays(1));
        updatedTask.setId(taskId);
        taskManager.updateTask(updatedTask);

        Task retrievedTask = taskManager.getTask(taskId);
        assertEquals("Updated Task", retrievedTask.getName());
        assertEquals("Updated Description", retrievedTask.getDescription());
        assertEquals(TaskStatus.DONE, retrievedTask.getStatus());
        assertEquals(Duration.ofHours(2), retrievedTask.getDuration());
        assertEquals(fixedTime.plusDays(1), retrievedTask.getStartTime());
    }

    @Test
    void shouldCorrectlyDeleteTask() {
        LocalDateTime fixedTime = LocalDateTime.of(2024, 6, 27, 10, 0);
        Task task = new Task("Delete Task", "Description", TaskStatus.NEW,
                Duration.ofHours(1), fixedTime);
        int taskId = taskManager.addTask(task).getId();
        taskManager.deleteTask(taskId);
        assertNull(taskManager.getTask(taskId));
    }


}
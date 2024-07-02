import manager.TaskManager;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @BeforeEach
    public abstract void setUp();

    @Test
    public void shouldAddTask() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Task task = new Task("Test", "Description", TaskStatus.NEW,
                Duration.ofHours(2), now);
        taskManager.addTask(task);
        assertNotNull(taskManager.getTask(task.getId()));
        assertEquals("Test", taskManager.getTask(task.getId()).getName());
        assertEquals("Description", taskManager.getTask(task.getId()).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getTask(task.getId()).getStatus());
        assertEquals(Duration.ofHours(2), taskManager.getTask(task.getId()).getDuration());
        assertEquals(now, taskManager.getTask(task.getId()).getStartTime());
    }

    @Test
    public void shouldUpdateTask() {
        LocalDateTime creationTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Task task = new Task("Test", "Description", TaskStatus.NEW,
                Duration.ofHours(2), creationTime);
        taskManager.addTask(task);
        LocalDateTime updateTime = creationTime.plusDays(1);
        task.setName("Updated Test");
        task.setDescription("Updated Description");
        task.setStatus(TaskStatus.DONE);
        task.setDuration(Duration.ofHours(3));
        task.setStartTime(updateTime);
        taskManager.updateTask(task);
        Task updatedTask = taskManager.getTask(task.getId());
        assertEquals("Updated Test", updatedTask.getName());
        assertEquals("Updated Description", updatedTask.getDescription());
        assertEquals(TaskStatus.DONE, updatedTask.getStatus());
        assertEquals(Duration.ofHours(3), updatedTask.getDuration());
        assertEquals(updateTime, updatedTask.getStartTime());
    }
}
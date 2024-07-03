package task;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void shouldCreateTaskWithCorrectFields() {
        LocalDateTime fixedTime = LocalDateTime.of(2024, 6, 27, 10, 0);
        Task task = new Task("Task Name", "Task Description", TaskStatus.NEW,
                Duration.ofHours(1), fixedTime);

        assertEquals("Task Name", task.getName());
        assertEquals("Task Description", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(Duration.ofHours(1), task.getDuration());
        assertEquals(fixedTime, task.getStartTime());
    }
}
package task;

import model.SubTask;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SubTaskTest {

    @Test
    public void testCreateSubTask() {
        String name = "Subtask 1";
        String description = "Description 1";
        TaskStatus status = TaskStatus.NEW;
        int epicId = 1;

        SubTask subTask = new SubTask(name, description, status, epicId, Duration.ofHours(1),
                LocalDateTime.now());

        assertEquals(name, subTask.getName());
        assertEquals(description, subTask.getDescription());
        assertEquals(status, subTask.getStatus());
        assertEquals(epicId, subTask.getEpicId());
    }

    @Test
    public void testSetEpicId() {
        SubTask subTask = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, 1,
                Duration.ofHours(3), LocalDateTime.now());
        subTask.setId(1);

        subTask.setEpicId(2);
        assertEquals(2, subTask.getEpicId());

        subTask.setEpicId(1);
        assertNotEquals(1, subTask.getEpicId());
    }

    @Test
    public void testEquals() {
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, 1,
                Duration.ofHours(5), LocalDateTime.now());
        SubTask subTask2 = new SubTask("Subtask 2", "Description 2", TaskStatus.IN_PROGRESS,
                2, Duration.ofHours(4), LocalDateTime.now());
        SubTask subTask3 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, 1,
                Duration.ofHours(1), LocalDateTime.now());

        assertNotEquals(subTask1, subTask2);
        assertEquals(subTask1, subTask3);
    }

    @Test
    public void testHashCode() {
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, 1,
                Duration.ofHours(4), LocalDateTime.now());
        SubTask subTask2 = new SubTask("Subtask 2", "Description 2", TaskStatus.IN_PROGRESS,
                2, Duration.ofHours(3), LocalDateTime.now());
        SubTask subTask3 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, 1,
                Duration.ofHours(1), LocalDateTime.now());

        assertNotEquals(subTask1.hashCode(), subTask2.hashCode());
        assertEquals(subTask1.hashCode(), subTask3.hashCode());
    }
}
package task;

import model.Epic;
import model.SubTask;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    void shouldCalculateDurationAndStartTimeForEpic() {
        Epic epic = new Epic("Epic Name", "Epic Description", TaskStatus.NEW,
                Duration.ofHours(5), LocalDateTime.now());
        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.NEW, 1,
                Duration.ofHours(2), LocalDateTime.now());
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", TaskStatus.NEW, 1,
                Duration.ofHours(3), LocalDateTime.now());

        epic.addSubTask(1);
        epic.addSubTask(2);

        assertEquals(Duration.ofHours(5), epic.getDuration());
        assertEquals(LocalDateTime.now(), epic.getStartTime());
    }
}
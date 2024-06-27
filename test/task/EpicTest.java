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
        LocalDateTime startTime = LocalDateTime.now(); // Сохраняем текущее время в переменную
        Epic epic = new Epic("Epic Name", "Epic Description", TaskStatus.NEW,
                Duration.ofHours(5), startTime);
        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.NEW, 1,
                Duration.ofHours(2), startTime);
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", TaskStatus.NEW, 1,
                Duration.ofHours(3), startTime);

        epic.addSubTask(subTask1.getId());
        epic.addSubTask(subTask2.getId());

        assertEquals(Duration.ofHours(5), epic.getDuration());
        assertEquals(startTime, epic.getStartTime()); // Используем сохранённое время
    }
}
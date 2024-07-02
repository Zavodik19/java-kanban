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
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic = new Epic("Epic Name", "Epic Description", TaskStatus.NEW,
                Duration.ofHours(5), startTime);
        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.NEW, 1,
                Duration.ofHours(2), startTime.plusHours(1));
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", TaskStatus.NEW, 1,
                Duration.ofHours(3), startTime.plusHours(3));

        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);

        // Пересчитываем время начала и продолжительность эпика
        LocalDateTime calculatedStartTime = epic.calculateStartTime();
        Duration calculatedDuration = epic.calculateDuration();

        assertEquals(calculatedDuration, epic.getDuration());
        assertEquals(calculatedStartTime, epic.getStartTime()); // Используем пересчитанное время
    }
}
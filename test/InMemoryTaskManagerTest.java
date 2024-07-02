import exception.TaskCrossingTimeException;
import manager.InMemoryTaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }


    @DisplayName("статус эпика устанавливается в NEW, если все его подзадачи имеют статус NEW")
    @Test
    public void shouldSetStatusNewWhenAllSubTasksAreNew() {
        LocalDateTime startTime = LocalDateTime.of(2024, 7, 2, 10, 0); // Фиксированное время начала
        Epic epic = new Epic("Epic Test", "Description", TaskStatus.NEW,
                Duration.ofHours(5), startTime);
        Epic addedEpic = taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.NEW,
                addedEpic.getId(), Duration.ofHours(1), startTime);
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", TaskStatus.NEW,
                addedEpic.getId(), Duration.ofHours(2),
                startTime.plusHours(1)); // Добавляем к начальному времени длительность первой подзадачи
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        assertEquals(2, taskManager.getAllSubTasks().size());

        taskManager.updateEpicStatus(addedEpic.getId());
        assertEquals(TaskStatus.NEW, taskManager.getEpic(addedEpic.getId()).getStatus(),
                "Статус эпика должен быть NEW перед обновлением");

        taskManager.updateEpicStatus(addedEpic.getId());
        assertEquals(TaskStatus.NEW, taskManager.getEpic(addedEpic.getId()).getStatus(),
                "Статус эпика должен оставаться NEW после обновления");
    }

    @DisplayName("статус эпика устанавливается в DONE, когда все подзадачи выполнены (DONE)")
    @Test
    public void shouldSetStatusDoneWhenAllSubTasksAreDone() {
        Epic epic = new Epic("Epic Test", "Description", TaskStatus.NEW,
                Duration.ofHours(5), LocalDateTime.now());
        Epic addedEpic = taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.DONE,
                addedEpic.getId(), Duration.ofHours(1), LocalDateTime.now());
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", TaskStatus.DONE,
                addedEpic.getId(), Duration.ofHours(1),
                LocalDateTime.now().plusHours(1));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.updateEpicStatus(addedEpic.getId());
        assertEquals(TaskStatus.DONE, taskManager.getEpic(addedEpic.getId()).getStatus());
    }

    @DisplayName("статус эпика устанавливается в IN_PROGRESS, если у эпика есть Подзадачи со статусами NEW и DONE")
    @Test
    public void shouldSetStatusInProgressWhenSubTasksAreNewAndDone() {
        Epic epic = new Epic("Epic Test", "Description", TaskStatus.NEW,
                Duration.ofHours(5), LocalDateTime.now());
        Epic addedEpic = taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.NEW,
                addedEpic.getId(), Duration.ofHours(1), LocalDateTime.now());
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", TaskStatus.DONE,
                addedEpic.getId(), Duration.ofHours(1),
                LocalDateTime.now().plusHours(1));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.updateEpicStatus(addedEpic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(addedEpic.getId()).getStatus());
    }

    @DisplayName("статус эпика устанавливается в IN_PROGRESS, если хотя бы одна подзадача имеет статус IN_PROGRESS")
    @Test
    public void shouldSetStatusInProgressWhenAnySubTaskIsInProgress() {
        Epic epic = new Epic("Epic Test", "Description", TaskStatus.NEW,
                Duration.ofHours(5), LocalDateTime.now());
        Epic addedEpic = taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.IN_PROGRESS,
                addedEpic.getId(), Duration.ofHours(1), LocalDateTime.now());
        taskManager.addSubTask(subTask1);

        taskManager.updateEpicStatus(addedEpic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(addedEpic.getId()).getStatus());
    }

    @Test
    void saveTaskInTreeSetShouldThrowExceptionWhenTasksOverlap() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW,
                Duration.ofHours(2), LocalDateTime.of(2024, Month.JULY, 2, 10, 0));
        taskManager.addTask(task1);

        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW,
                Duration.ofHours(2), LocalDateTime.of(2024, Month.JULY, 2, 11, 0));

        Executable executable = () -> taskManager.saveTaskInTreeSet(task2);
        assertThrows(TaskCrossingTimeException.class, executable,
                "Должно быть выброшено исключение, так как задачи пересекаются по времени.");
    }


    @Test
    void updateEpicStatusShouldCorrectlyUpdateStatusBasedOnSubTasks() {
        LocalDateTime epicStartTime = LocalDateTime.of(2024, Month.JULY, 3, 9, 0);
        Duration epicDuration = Duration.ofHours(4);
        Epic epic = new Epic("Epic 1", "Description 1", TaskStatus.NEW, epicDuration, epicStartTime);
        taskManager.addEpic(epic);

        LocalDateTime startTime1 = LocalDateTime.of(2024, Month.JULY, 3, 9, 0);
        Duration duration1 = Duration.ofHours(2);
        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.NEW, epic.getId(), duration1, startTime1);
        taskManager.addSubTask(subTask1);

        LocalDateTime startTime2 = LocalDateTime.of(2024, Month.JULY, 3, 11, 0);
        Duration duration2 = Duration.ofHours(2);
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", TaskStatus.NEW, epic.getId(), duration2, startTime2);
        taskManager.addSubTask(subTask2);

        taskManager.updateEpicStatus(epic.getId());
        assertEquals(TaskStatus.NEW, epic.getStatus(),
                "Статус эпика должен быть NEW, так как все подзадачи NEW.");

        subTask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask1);
        taskManager.updateEpicStatus(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Статус эпика должен быть IN_PROGRESS, так как одна подзадача DONE, а другая NEW.");

        subTask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask2);
        taskManager.updateEpicStatus(epic.getId());
        assertEquals(TaskStatus.DONE, epic.getStatus(),
                "Статус эпика должен быть DONE, так как все подзадачи DONE.");
    }
}
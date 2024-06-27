import manager.InMemoryTaskManager;
import model.Epic;
import model.SubTask;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }


    @DisplayName("статус эпика устанавливается в NEW, если все его подзадачи имеют статус NEW")
    @Test
    public void shouldSetStatusNewWhenAllSubTasksAreNew() {
        Epic epic = new Epic("Epic Test", "Description", TaskStatus.NEW,
                Duration.ofHours(5), LocalDateTime.now());
        Epic addedEpic = taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.NEW,
                addedEpic.getId(), Duration.ofHours(1), LocalDateTime.now());
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", TaskStatus.NEW,
                addedEpic.getId(), Duration.ofHours(1),
                LocalDateTime.now().plusHours(1));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.updateEpicStatus(addedEpic.getId());
        assertEquals(TaskStatus.NEW, taskManager.getEpic(addedEpic.getId()).getStatus());
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
}
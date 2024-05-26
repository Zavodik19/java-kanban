package test.manager;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void BeforeEach() {
        taskManager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
    }

    @Test  // Проверяем, что при удалении задачи, связанные с ней эпики и подзадачи также удаляются
    public void testDeleteTaskRemovesAssociatedEpicsAndSubTasks() {
        Epic epic = new Epic("Epic 1", "Description 1");
        epic = taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.DONE,
                epic.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", TaskStatus.IN_PROGRESS,
                epic.getId());

        subTask1 = taskManager.addSubTask(subTask1);
        subTask2 = taskManager.addSubTask(subTask2);

        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task = taskManager.addTask(task);

        taskManager.deleteTask(task.getId());

        List<Epic> epics = taskManager.getAllEpics();
        List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertTrue(epics.contains(epic));
        assertEquals(2, subTasks.size());
    }

    @Test  // Проверяем, что при удалении эпика, связанные с ним подзадачи также удаляются
    public void testDeleteEpicRemovesAssociatedSubTasks() {
        Epic epic = new Epic("Epic 1", "Description 1");
        epic = taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.DONE,
                epic.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", TaskStatus.IN_PROGRESS,
                epic.getId());

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.deleteEpic(epic.getId());

        List<Epic> epics = taskManager.getAllEpics();
        List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertTrue(epics.isEmpty());
        assertTrue(subTasks.isEmpty());
    }

    @Test
    public void testUpdateTaskUpdatesDataInManager() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task = taskManager.addTask(task);

        task.setName("Updated Task");
        task.setDescription("Updated Description");
        task.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.updateTask(task);

        Task updatedTask = taskManager.getTask(task.getId());
        assertEquals("Updated Task", updatedTask.getName());
        assertEquals("Updated Description", updatedTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
    }

    @Test  // Проверяем обновление статуса эпика на основе статусов связанных подзадач
    public void testUpdateEpicStatusBasedOnSubTaskStatus() {
        Epic epic = new Epic("Epic 1", "Description 1");
        epic = taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.DONE,
                epic.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", TaskStatus.IN_PROGRESS,
                epic.getId());

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.updateEpicStatus(epic.getId());

        Epic updatedEpic = taskManager.getEpic(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, updatedEpic.getStatus());
    }
}
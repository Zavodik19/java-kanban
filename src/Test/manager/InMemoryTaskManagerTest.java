package Test.manager;

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

    @Test
    public void testAddAndFindTasks() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW);

        task1 = taskManager.addTask(task1);
        task2 = taskManager.addTask(task2);

        Task foundTask1 = taskManager.getTask(task1.getId());
        Task foundTask2 = taskManager.getTask(task2.getId());

        assertEquals(task1, foundTask1);
        assertEquals(task2, foundTask2);
    }

    @Test
    public void testAddAndFindEpics() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");

        epic1 = taskManager.addEpic(epic1);
        epic2 = taskManager.addEpic(epic2);

        Epic foundEpic1 = taskManager.getEpic(epic1.getId());
        Epic foundEpic2 = taskManager.getEpic(epic2.getId());

        assertEquals(epic1, foundEpic1);
        assertEquals(epic2, foundEpic2);
    }

    @Test
    public void testAddAndFindSubTasks() {
        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.NEW, 1);
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", TaskStatus.NEW, 2);

        subTask1 = taskManager.addSubTask(subTask1);
        subTask2 = taskManager.addSubTask(subTask2);

        SubTask foundSubTask1 = taskManager.getSubTask(subTask1.getId());
        SubTask foundSubTask2 = taskManager.getSubTask(subTask2.getId());

        assertEquals(subTask1, foundSubTask1);
        assertEquals(subTask2, foundSubTask2);
    }

    @Test
    public void testDeleteTaskForId() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task = taskManager.addTask(task);

        taskManager.deleteTask(task.getId());

        assertNull(taskManager.getTask(task.getId()));
    }

    @Test
    public void testDeleteEpicForId() {
        Epic epic = new Epic("Epic 1", "Description 1");
        epic = taskManager.addEpic(epic);

        taskManager.deleteEpic(epic.getId());

        assertNull(taskManager.getEpic(epic.getId()));
    }

    @Test
    public void testDeleteSubTaskForId() {
        SubTask subTask = new SubTask("SubTask 1", "Description 1", TaskStatus.NEW, 1);
        subTask = taskManager.addSubTask(subTask);

        taskManager.deleteSubTask(subTask.getId());

        assertNull(taskManager.getSubTask(subTask.getId()));
    }

    @Test
    public void testUpdateTask() {
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

    @Test
    public void testUpdateEpicStatus() {
        Epic epic = new Epic("Epic 1", "Description 1");
        epic = taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.DONE, epic.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", TaskStatus.IN_PROGRESS, epic.getId());

        subTask1 = taskManager.addSubTask(subTask1);
        subTask2 = taskManager.addSubTask(subTask2);

        taskManager.updateEpicStatus(epic.getId());

        Epic updatedEpic = taskManager.getEpic(epic.getId());

        assertEquals(TaskStatus.IN_PROGRESS, updatedEpic.getStatus());
    }

    @Test
    public void testGetAllTasks() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW);

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        List<Task> allTasks = taskManager.getAllTask();

        assertEquals(2, allTasks.size());
        assertTrue(allTasks.contains(task1));
        assertTrue(allTasks.contains(task2));
    }

    @Test
    public void testGetAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        List<Epic> allEpics = taskManager.getAllEpics();

        assertEquals(2, allEpics.size());
        assertTrue(allEpics.contains(epic1));
        assertTrue(allEpics.contains(epic2));
    }

    @Test
    public void testGetAllSubTasks() {
        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.NEW, 1);
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", TaskStatus.NEW, 2);

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        List<SubTask> allSubTasks = taskManager.getAllSubTasks();

        assertEquals(2, allSubTasks.size());
        assertTrue(allSubTasks.contains(subTask1));
        assertTrue(allSubTasks.contains(subTask2));
    }

    @Test
    public void testDeleteAllTasks() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW);

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.deleteAllTask();

        List<Task> allTasks = taskManager.getAllTask();

        assertEquals(0, allTasks.size());
    }

    @Test
    public void testDeleteAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        taskManager.deleteAllEpic();

        List<Epic> allEpics = taskManager.getAllEpics();

        assertEquals(0, allEpics.size());
    }

    @Test
    public void testDeleteAllSubTasks() {
        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.NEW, 1);
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", TaskStatus.NEW, 2);

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.deleteAllSubTask();

        List<SubTask> allSubTasks = taskManager.getAllSubTasks();

        assertEquals(0, allSubTasks.size());
    }
}